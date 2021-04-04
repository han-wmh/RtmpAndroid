package com.han.rtmpandroid.utils

import android.util.Log
import timber.log.Timber
import timber.log.Timber.DebugTree
import java.util.*

/**
 * 日志工具
 */
class AndroidLogger {

    companion object {
        fun init(isDebug: Boolean) {
            if (isDebug) {
                Timber.plant(DebugTree())
            }
        }

        fun printIntI(tag: String?, value: Int) {
            Timber.tag(tag).i(String.format(Locale.ENGLISH, "%d", value))
        }

        fun printIntV(tag: String?, value: Int) {
            Timber.tag(tag).v(String.format(Locale.ENGLISH, "%d", value))
        }

        fun printIntD(tag: String?, value: Int) {
            Timber.tag(tag).d(String.format(Locale.ENGLISH, "%d", value))
        }

        fun printIntE(tag: String?, value: Int) {
            Timber.tag(tag).e(String.format(Locale.ENGLISH, "%d", value))
        }

        fun printIntWtf(tag: String?, value: Int) {
            Timber.tag(tag).wtf(String.format(Locale.ENGLISH, "%d", value))
        }

        fun printIntW(tag: String?, value: Int) {
            Timber.tag(tag).w(String.format(Locale.ENGLISH, "%d", value))
        }

        // long
        fun printLongI(tag: String?, value: Long) {
            Timber.tag(tag).i(String.format(Locale.ENGLISH, "%d", value))
        }

        fun printLongV(tag: String?, value: Long) {
            Timber.tag(tag).v(String.format(Locale.ENGLISH, "%d", value))
        }

        fun printLongD(tag: String?, value: Long) {
            Timber.tag(tag).d(String.format(Locale.ENGLISH, "%d", value))
        }

        fun printLongE(tag: String?, value: Long) {
            Timber.tag(tag).e(String.format(Locale.ENGLISH, "%d", value))
        }

        fun printLongWtf(tag: String?, value: Long) {
            Timber.tag(tag).wtf(String.format(Locale.ENGLISH, "%d", value))
        }

        fun printLongW(tag: String?, value: Long) {
            Timber.tag(tag).w(String.format(Locale.ENGLISH, "%d", value))
        }

        // 浮点
        fun printDoubleI(tag: String?, value: Long) {
            Timber.tag(tag).i(String.format(Locale.ENGLISH, "%f", value))
        }

        fun printDoubleV(tag: String?, value: Long) {
            Timber.tag(tag).v(String.format(Locale.ENGLISH, "%f", value))
        }

        fun printDoubleD(tag: String?, value: Long) {
            Timber.tag(tag).d(String.format(Locale.ENGLISH, "%f", value))
        }

        fun printDoubleE(tag: String?, value: Long) {
            Timber.tag(tag).e(String.format(Locale.ENGLISH, "%f", value))
        }

        fun printDoubleWtf(tag: String?, value: Long) {
            Timber.tag(tag).wtf(String.format(Locale.ENGLISH, "%f", value))
        }

        fun printDoubleW(tag: String?, value: Long) {
            Timber.tag(tag).w(String.format(Locale.ENGLISH, "%f", value))
        }

        // throw
        fun printThrowI(tag: String?, throwable: Throwable?) {
            Timber.tag(tag).i(throwable)
        }

        fun printThrowV(tag: String?, throwable: Throwable?) {
            Timber.tag(tag).v(throwable)
        }

        fun printThrowD(tag: String?, throwable: Throwable?) {
            Timber.tag(tag).d(throwable)
        }

        fun printThrowE(tag: String?, throwable: Throwable?) {
            Timber.tag(tag).e(throwable)
        }

        fun printThrowW(tag: String?, throwable: Throwable?) {
            Timber.tag(tag).w(throwable)
        }

        fun printThrowWtf(tag: String?, throwable: Throwable?) {
            Timber.tag(tag).wtf(throwable)
        }

        // object
        fun printStringI(tag: String?, vararg strs: Any?) {
            Timber.tag(tag).i("%s", *strs)
        }

        fun printStringV(tag: String?, vararg strs: Any?) {
            Timber.tag(tag).v("%s", *strs)
        }

        fun printStringD(tag: String?, vararg strs: Any?) {
            Timber.tag(tag).d("%s", *strs)
        }

        fun printStringE(tag: String?, vararg strs: Any?) {
            Timber.tag(tag).e("%s", *strs)
        }

        fun printStringW(tag: String?, vararg strs: Any?) {
            Timber.tag(tag).w("%s", *strs)
        }

        fun printStringWtf(tag: String?, vararg strs: Any?) {
            Timber.tag(tag).wtf("%s", *strs)
        }

        // date
        fun printDateI(tag: String?, date: Date?) {
            if (date == null) {
                return
            }
            Timber.tag(tag).i(DateConvertor.getDateStr(date))
        }

        fun printDateW(tag: String?, date: Date?) {
            if (date == null) {
                return
            }
            Timber.tag(tag).w(DateConvertor.getDateStr(date))
        }

        fun printDateV(tag: String?, date: Date?) {
            if (date == null) {
                return
            }
            Timber.tag(tag).v(DateConvertor.getDateStr(date))
        }

        fun printDateD(tag: String?, date: Date?) {
            if (date == null) {
                return
            }
            Timber.tag(tag).d(DateConvertor.getDateStr(date))
        }

        fun printDateE(tag: String?, date: Date?) {
            if (date == null) {
                return
            }
            Timber.tag(tag).i(DateConvertor.getDateStr(date))
        }

        fun printDateWtf(tag: String?, date: Date?) {
            if (date == null) {
                return
            }
            Timber.tag(tag).wtf(DateConvertor.getDateStr(date))
        }

        /**
         * 获取堆栈信息
         *
         * @param throwable
         * @return
         */
        fun getStackTraceString(throwable: Throwable?): String? {
            return Log.getStackTraceString(throwable)
        }
    }
}