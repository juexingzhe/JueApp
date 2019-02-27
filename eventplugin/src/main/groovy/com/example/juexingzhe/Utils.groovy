package com.example.juexingzhe;

class Utils {

    /**
     * 首字母大写
     */
    static String captureName(String name) {
        char[] cs = name.toCharArray()
        cs[0] -= 32
        return String.valueOf(cs);
    }

    /**
     * 获取当前 variant 的其中一个 asset 目录
     */
    static File getOneAssetDir(Object variant) {
        if (variant.sourceSets != null && variant.sourceSets.size() > 0) {
            Object[] arrays = variant.sourceSets.get(0).assets.srcDirs.toArray()
            if (arrays.length > 0) {
                return arrays[0]
            }
        }

        return null
    }
}