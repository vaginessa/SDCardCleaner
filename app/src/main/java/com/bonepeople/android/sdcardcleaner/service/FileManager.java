package com.bonepeople.android.sdcardcleaner.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.SparseArray;

import com.bonepeople.android.sdcardcleaner.Global;
import com.bonepeople.android.sdcardcleaner.models.SDFile;
import com.bonepeople.android.sdcardcleaner.thread.CleanFileThread;
import com.bonepeople.android.sdcardcleaner.thread.DeleteFileThread;
import com.bonepeople.android.sdcardcleaner.thread.ScanFileThread;

public class FileManager extends Service {
    public static final int STATE_READY = 0;
    public static final int STATE_SCAN_EXECUTING = 1;
    public static final int STATE_SCAN_STOP = 2;
    public static final int STATE_SCAN_FINISH = 3;
    public static final int STATE_CLEAN_EXECUTING = 4;
    public static final int STATE_CLEAN_STOP = 5;
    public static final int STATE_CLEAN_FINISH = 6;
    public static final int STATE_DELETE_EXECUTING = 7;
    public static final int STATE_DELETE_STOP = 8;
    public static final int STATE_DELETE_FINISH = 9;
    private static int state = STATE_READY;

    /**
     * 重置状态为未扫描的状态，根文件异常丢失的情况使用
     */
    public static void reset() {
        state = STATE_READY;
        Global.reset();
    }

    /**
     * 开始扫描文件
     */
    public static void startScan() {
        if (state == STATE_READY || state == STATE_SCAN_FINISH || state == STATE_CLEAN_FINISH || state == STATE_DELETE_FINISH) {
            state = STATE_SCAN_EXECUTING;
            Global.reset();
            new ScanFileThread().start();
        }
    }

    /**
     * 停止扫描文件
     */
    public static void stopScan() {
        if (state == STATE_SCAN_EXECUTING) {
            state = STATE_SCAN_STOP;
        }
    }

    /**
     * 扫描文件结束，该方法仅由扫描的线程调用
     */
    public static void finishScan() {
        state = STATE_SCAN_FINISH;
    }

    /**
     * 开始清理文件
     */
    public static void startClean() {
        if (state == STATE_SCAN_FINISH || state == STATE_CLEAN_FINISH || state == STATE_DELETE_FINISH) {
            state = STATE_CLEAN_EXECUTING;
            new CleanFileThread().start();
        }
    }

    /**
     * 停止清理文件
     */
    public static void stopClean() {
        if (state == STATE_CLEAN_EXECUTING) {
            state = STATE_CLEAN_STOP;
        }
    }

    /**
     * 清理文件结束，该方法仅由清理的线程调用
     */
    public static void finishClean() {
        state = STATE_CLEAN_FINISH;
    }

    /**
     * 开始删除文件
     */
    public static void startDelete(SparseArray<SDFile> files) {
        if (state == STATE_SCAN_FINISH || state == STATE_CLEAN_FINISH || state == STATE_DELETE_FINISH) {
            state = STATE_DELETE_EXECUTING;
            new DeleteFileThread(files).start();
        }
    }

    /**
     * 停止删除文件
     */
    public static void stopDelete() {
        if (state == STATE_DELETE_EXECUTING) {
            state = STATE_DELETE_STOP;
        }
    }

    /**
     * 删除文件结束，该方法仅由删除的线程调用
     */
    public static void finishDelete() {
        state = STATE_DELETE_FINISH;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {

    }

    public static int getState() {
        return state;
    }
}
