package com.mikuwxc.autoreply.wcutil;

import java.io.File;

public class AuthUtil {
    public static void clearAllForbiddenAuth() {
        File[] listFiles = new File(GlobalUtil.FORBIDDEN_AUTH_FILE_SAVE_ROOT_PATH_).listFiles();
        if (listFiles != null) {
            for (File delete : listFiles) {
                delete.delete();
            }
        }
    }

    public static void createForbiddenAuthFile(int i) throws Exception {
        File file = new File(GlobalUtil.FORBIDDEN_AUTH_FILE_SAVE_ROOT_PATH_ + i);
        if (!file.exists()) {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            file.createNewFile();
        }
    }

    public static boolean isForbiddenAuth(int i) {
        return new File(GlobalUtil.FORBIDDEN_AUTH_FILE_SAVE_ROOT_PATH_ + i).exists();
    }
}