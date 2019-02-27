package com.example.juexingzhe

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.attributes.Attribute
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Delete

class PluginImpl implements Plugin<Project> {

    private Project project

    /** aar 内 event 文件夹名，用于打包和解包时做 eve 拷贝 */
    private static final String eventFolder = 'eventeve'

    @Override
    void apply(Project project) {
        this.project = project
        System.out.println("project is " + project.name)

        project.extensions.add('EventPluginExt', EventPluginExt)

        if (!isLibrary()) {
            // 编译apk时忽略app依赖中的assets/*.event aapt中通过:!连接多个需要忽略的选项
            String ignoreAssets = project.android.aaptOptions.ignoreAssets
            if (ignoreAssets == null) {
                ignoreAssets = '!*.event'
            } else {
                if (!ignoreAssets.contains('!*.event')) {
                    ignoreAssets += ':!*.event'
                }
            }
            project.android.aaptOptions.ignoreAssets = ignoreAssets
        }

        project.afterEvaluate {

            def eventModule = project.rootProject.childProjects.get(project.EventPluginExt.eventModuleName)
            if (eventModule == project) {
                throw new GradleException('event module should not be itself!')
            }

            if (eventModule == null || !eventModule.projectDir.exists()) {
                throw new GradleException('event module is not exists')
            }

            def cleanEventTask = project.task("eventClean", type: Delete) {
                delete("${project.rootProject.projectDir}/${eventModule.name}/src/main/java")
            }

            project.tasks.getByName("clean").dependsOn cleanEventTask

            getVariants().all { variant ->
                def variantName = Utils.captureName(variant.name)
                System.out.println("variantName is " + variantName)
                if (isLibrary()) {
                    System.out.println("=================== is Library")
                    def intoAsset
                    def assetDir = Utils.getOneAssetDir(variant)
                    if (assetDir == null) {
                        intoAsset = "${project.projectDir}/src/main/assets/$eventFolder"
                    } else {
                        intoAsset = assetDir.absolutePath + "/$eventFolder"
                    }

                    def intoEventSdk = "${project.rootProject.projectDir}/${eventModule.name}/src/main/java"

                    List list = new ArrayList()
                    variant.sourceSets.each { sourceSet ->
                        sourceSet.java.srcDirs.each { file ->
                            list.add(file)
                        }
                    }

                    def javaSrcList = list as String[]
                    System.out.println("javaSrcList is " + javaSrcList)

                    // 1. 定义拷贝任务A——src/javasrc/*/*.event->event/src/javasrc/*/*.java
                    // 这部分拷贝生成的文件，在执行preBuild前生成，需要单独执行clean任务才可以删除
                    Task eventLibCopyTask = project.task("eventLibCopyTaskFor" + variantName, type: Copy) {
                        includeEmptyDirs = false
                        from javaSrcList
                        into intoEventSdk
                        include "**/*.${project.EventPluginExt.postFix}"
                        rename "(.*)${project.EventPluginExt.postFix}", '$1java'
                    }

                    // 2. 获取preBuild任务B
                    Task preBuildTask = project.tasks.getByName("pre" + variantName + "Build")

                    // 3. 定义拷贝任务C——src/javasrc/*/*.event->assets/cocoFolder/*/*.event
                    // 这部分拷贝生成的文件，在执行generateAssets前生成，在packageAssets任务之后删除
                    Task eventAssetsCopyTask = project.task("eventAssetsCopyTaskFor" + variantName, type: Copy) {
                        includeEmptyDirs = false
                        from javaSrcList
                        into intoAsset
                        include "**/*.${project.EventPluginExt.postFix}"
                    }

                    // 4. 获取generateAssets任务D
                    Task generateAssetsTask = project.tasks.getByName("generate" + variantName + "Assets")

                    // 5. 获取mergeAssets任务E
                    Task mergeAssetsTask = variant.mergeAssets

                    // 6. 获取当前variant编译任务F——如assembleDebug/assembleRease
                    Task assembleTask = variant.assemble

                    // 7. 定义删除任务G——delete assets/cocoFolder/*/*.event
                    Task eventAssetsDeleteTask = project.task("eventAssetsDeleteFor" + variantName, type: Delete) {
                        delete intoAsset
                    }

                    // 8. 定义任务执行顺序
                    // 1) 拷贝lib中event到event中
                    // 2) preBuild
                    // 3) 拷贝lib中event到assets中
                    // 4) generateAssets
                    // 5) mergeAssets
                    // 6) 删除assets中的event文件
                    // 7) assemble
                    // 最终结果为
                    // 1) prebuild时，lib中的event会拷贝到event中，编译可以正常执行
                    // 2) assemble时，编译产物中assets中包含由event产物构成的eventFolder文件夹
                    preBuildTask.dependsOn eventLibCopyTask

                    generateAssetsTask.dependsOn eventAssetsCopyTask
                    mergeAssetsTask.finalizedBy eventAssetsDeleteTask
                    assembleTask.finalizedBy eventAssetsDeleteTask

                    // 添加event的预编译依赖，避免event模块在编译完成之后再执行当前project的event拷贝任务，导致类查找不到的问题
                    def preEventBuildTask = null
                    try{
                        preEventBuildTask = eventModule.tasks.getByName("preBuild")
                    } catch (Exception ignored) {

                    }

                    if(preEventBuildTask != null) {
                        preEventBuildTask.mustRunAfter eventLibCopyTask
                    }
                } else {
                    System.out.println("=================== is apk")
                    // app下，对依赖的aar内assets/eventeve内bpi做拷贝处理

                    // 1. 对该variant下所有依赖做循环，将其中aar做解包并做拷贝任务——app/aar/assets/eventFolder->event/src/java
                    Task appUnpackTask = null
                    if (variant.hasProperty("compileConfiguration")) {
                        // For Android Gradle plugin >= 2.5
                        Attribute artifactType = Attribute.of("artifactType", String)
                        FileCollection classPathConfig = variant.compileConfiguration.incoming.artifactView {
                            attributes {
                                it.attribute(artifactType, "aar")
                            }
                        }.files
                        System.out.println("classPathConfig = " + classPathConfig)

                        appUnpackTask = project.task("eventAppUnpack" + variantName, type: Copy) {
                            includeEmptyDirs = false
                            classPathConfig.each {
                                from(project.zipTree(it))
                            }
                            into "${project.rootProject.projectDir}/${eventModule.name}/src/main/java"
                            include "**/$eventFolder/**/*.${project.EventPluginExt.postFix}"
                            rename "(.*)${project.EventPluginExt.postFix}", '$1java'
                            eachFile {
                                it.path = it.path.replaceFirst(".*$eventFolder", '')
                            }
                        }
                    } else {

                    }

                    // 2.定义app下的拷贝任务——javasrc/*.event->event/src/javasrc/*/*.java
                    // 这部分拷贝生成的文件，在执行preBuild前生成，需要单独执行clean任务才可以删除
                    List list = new ArrayList()
                    variant.sourceSets.each { sourceSet ->
                        sourceSet.java.srcDirs.each { file ->
                            list.add(file)
                        }
                    }
                    def javaSrcList = list as String[]
                    System.out.println("javaSrcList is " + javaSrcList)

                    def intoEventSdk = "${project.rootProject.projectDir}/${eventModule.name}/src/main/java"

                    Task eventAppCopyTask = project.task("eventAppCopyTaskFor" + variantName, type: Copy) {
                        includeEmptyDirs = false
                        from javaSrcList
                        into intoEventSdk
                        include "**/*.${project.EventPluginExt.postFix}"
                        rename "(.*)${project.EventPluginExt.postFix}", '$1java'
                    }

                    if (appUnpackTask != null) {
                        Task preBuildTask = project.tasks.getByName("pre" + variantName + "Build")
                        preBuildTask.dependsOn(appUnpackTask)
                        preBuildTask.dependsOn(eventAppCopyTask)

                        eventModule.tasks.getByName("preBuild").mustRunAfter appUnpackTask
                        eventModule.tasks.getByName("preBuild").mustRunAfter eventAppCopyTask
                    }
                }
            }
        }


    }

    /**
     * 获取当前 project 所有 variant
     * @return
     */
    private Object getVariants() {
        return project.android.hasProperty('libraryVariants') ?
                project.android.libraryVariants : project.android.applicationVariants
    }

    /**
     * 是否是 lib 模块
     * @return
     */
    private boolean isLibrary() {
        return project.android.hasProperty('libraryVariants')
    }
}