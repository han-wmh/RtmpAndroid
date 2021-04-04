package com.han.rtmpandroid.utils

import android.text.TextUtils
import java.io.File
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.*

/**
 * 日期时间工具
 */
class DateConvertor {
    companion object {

        /**
         * 时间字串
         */
        private var newDateStr: String? = null
        /**
         * 时间
         */
        private val newDate: Date? = null

        /**
         * yyyy-MM-dd HH:mm:ss型时间格式
         */
        private val timeFormatStr = "yyyy-MM-dd HH:mm:ss"
        /**
         * 日期格式化
         */
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        /**
         * 获取当前时间字串
         *
         * @return 当前时间字串
         */
        fun getDateStr(): String? {
            return getDateStr(Date())
        }

        /**
         * 获取当前时间字串
         *
         * @param date 时间
         * @return 当前时间字串
         */
        fun getDateStr(date: Date?): String? {
            return if (date == null) {
                newDateStr = "null"
                newDateStr
            } else {
                try {
                    val sim = SimpleDateFormat(timeFormatStr, Locale.getDefault())
                    sim.format(date)
                } catch (ex: Exception) {
                    newDateStr = "null"
                    newDateStr
                }
            }
        }

        /**
         * 获取当前时间字串
         *
         * @param value long型UTC时间
         * @return 当前时间字串
         */
        fun getDateStr(value: Long): String? {
            return try {
                val sim = SimpleDateFormat(timeFormatStr, Locale.getDefault())
                sim.format(Date(value))
            } catch (ex: Exception) {
                newDateStr = "null"
                newDateStr
            }
        }

        /**
         * 时间字串转时间
         *
         * @param dateFormat 时间字串格式
         * @param dateStr    时间字串
         * @return 时间
         */
        fun getDateByStr(dateFormat: String?, dateStr: String?): Date? {
            val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
            val pos = ParsePosition(0)
            return formatter.parse(dateStr, pos)
        }

        /**
         * 获取"yyyy-MM-dd HH:mm:ss"格式时间
         *
         * @param date 时间
         * @return "yyyy-MM-dd HH:mm:ss"格式时间字串
         */
        fun getFormatDateStr(date: Date?): String? {
            try {
                val sf = SimpleDateFormat(timeFormatStr, Locale.getDefault())
                newDateStr = sf.format(date)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return newDateStr
        }

        /**
         * 获取所给格式时间
         *
         * @param date          时间
         * @param dateFormatStr 时间格式
         * @return 所给格式时间字串
         */
        fun getFormatDateStr(date: Date?, dateFormatStr: String?): String? {
            try {
                val sf = SimpleDateFormat(dateFormatStr, Locale.getDefault())
                newDateStr = sf.format(date)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return newDateStr
        }

        /**
         * 获取"yyyyMMdd_HHmmss"格式时间，用于文件名
         *
         * @return "yyyyMMdd_HHmmss"格式时间字串
         */
        fun getNameFormDate(): String? {
            return getNameFormDate(Date())
        }

        /**
         * 获取"yyyyMMdd_HHmmss"格式时间，用于文件名
         *
         * @param date 时间
         * @return "yyyyMMdd_HHmmss"格式时间字串
         */
        fun getNameFormDate(date: Date?): String? {
            return getNameFormDate(date, "")
        }

        /**
         * 获取"yyyyMMdd_HHmmss"格式时间，用于文件名
         *
         * @param date 时间
         * @return "yyyyMMdd_HHmmss"格式时间字串
         */
        fun getNameFormDate(date: Date?, format: String?): String? {
            var format = format
            if (TextUtils.isEmpty(format)) {
                format = "yyyyMMdd_HHmmss"
            }
            return getFormatDateStr(date, format)
        }

        /**
         * 获取文件的创建时间，格式"yyyy-MM-dd"格
         *
         * @param path 文件路径
         * @return yyyy-MM-dd"格式时间字串
         */
        fun getNameFormDate(path: String?): String? {
            try {
                newDateStr = dateFormat.format(Date(File(path).lastModified()))
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return newDateStr
        }

        /**
         * 获取"yyyy-MM-dd"格式时间，用于文件名
         *
         * @param date 时间
         * @return yyyy-MM-dd"格式时间字串
         */
        fun getNameForDate(date: Date?): String? {
            try {
                newDateStr = dateFormat.format(date)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return newDateStr
        }

        /**
         * 获取某天0时/24时时间,
         * 需注意传入的月份应比实际月份少1(Calendar月份从0开始)
         *
         * @return 当日0时/24时时间
         */
        fun getTodayRange(): Array<Long?>? {
            return getCurrentDateRange(Date())
        }

        /**
         * 获取某天0时/24时时间,
         * 需注意传入的月份应比实际月份少1(Calendar月份从0开始)
         *
         * @param date Date
         * @return Date当日0时/24时时间
         */
        fun getCurrentDateRange(date: Date?): Array<Long?>? {
            return arrayOf(getStartTime(date), getEndTime(date))
        }

        /**
         * 获取当前时间的Calendar对象
         *
         * @param date 当前时间
         * @return 当前时间的Calendar对象
         */
        fun getCurrentCalendar(date: Date?): Calendar {
            val calendar = Calendar.getInstance()
            calendar.time = date
            return calendar
        }

        /**
         * 获取当前日期0时时间
         *
         * @return 当前日期0时时间
         */
        fun getStartTime(): Long {
            val calendar = Calendar.getInstance()
            return getStartTime(calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DATE])
        }

        /**
         * 获取当前日期24时时间
         *
         * @return 当前日期24时时间
         */
        fun getEndTime(): Long {
            val calendar = Calendar.getInstance()
            return getEndTime(calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DATE])
        }

        /**
         * 获取当前日期0时时间
         *
         * @param date 当前时间
         * @return 当前日期0时时间
         */
        fun getStartTime(date: Date?): Long {
            val calendar = getCurrentCalendar(date)
            return getStartTime(calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DATE])
        }

        /**
         * 获取当前日期24时时间
         *
         * @param date 当前时间
         * @return 当前日期24时时间
         */
        fun getEndTime(date: Date?): Long {
            val calendar = getCurrentCalendar(date)
            return getEndTime(calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DATE])
        }

        /**
         * 根据提供的时间间隔,第几个间隔等信息获取时间段([x, y])
         * eg.:
         * Today:2017-06-13
         * 今天之前第3个7天时间段(若第1个,则含今天):
         * getTimeIntervalByPeriod(7, 2, true)==>{1495526400000(2017-05-24 00:00:00),1496131199999(2017-05-30 23:59:59)}
         * 今天之后第3个7天时间段(若第1个,含今天):
         * getTimeIntervalByPeriod(7, 2, false)==>{1498464000000(2017-06-27 00:00:00),1499068799999(2017-07-03 23:59:59)}
         *
         * @param interval 时间间隔(天)
         * @param period   时间周期(第几个间隔,从0开始)
         * @param isBefore 是否是今天之前
         * @return 时间段([x, y], x为当日0时, y为当日24时)
         */
        fun getTimeIntervalByPeriod(interval: Int, period: Int, isBefore: Boolean): LongArray? {
            val baseFactor: Int
            baseFactor = if (isBefore) {
                -1
            } else {
                1
            }
            val calendar = Calendar.getInstance()
            val upperCalendar = Calendar.getInstance()
            val lowerCalendar = Calendar.getInstance()
            if (isBefore) {
                upperCalendar.time = calendar.time
                upperCalendar.add(Calendar.DATE, baseFactor * Math.abs(interval * period))
                lowerCalendar.time = upperCalendar.time
                lowerCalendar.add(Calendar.DATE, baseFactor * Math.abs(interval - 1))
            } else {
                lowerCalendar.time = calendar.time
                lowerCalendar.add(Calendar.DATE, baseFactor * Math.abs(interval * period))
                upperCalendar.time = lowerCalendar.time
                upperCalendar.add(Calendar.DATE, baseFactor * Math.abs(interval - 1))
            }
            return longArrayOf(getStartTime(lowerCalendar.time), getEndTime(upperCalendar.time))
        }

        /**
         * 获取某天0时时间,
         * 需注意传入的月份应比实际月份少1(Calendar月份从0开始)
         *
         * @param year  年
         * @param month 月
         * @param day   日
         * @return 当日0时时间
         */
        fun getStartTime(year: Int, month: Int, day: Int): Long {
            val todayStart = Calendar.getInstance()
            todayStart[year, month] = day
            todayStart[Calendar.HOUR_OF_DAY] = 0
            todayStart[Calendar.MINUTE] = 0
            todayStart[Calendar.SECOND] = 0
            todayStart[Calendar.MILLISECOND] = 0
            return todayStart.time.time
        }

        /**
         * 获取某天24时时间
         *
         * @param year  年
         * @param month 月
         * @param day   日
         * @return 当日24时时间
         */
        fun getEndTime(year: Int, month: Int, day: Int): Long {
            val todayEnd = Calendar.getInstance()
            todayEnd[year, month] = day
            todayEnd[Calendar.HOUR_OF_DAY] = 23
            todayEnd[Calendar.MINUTE] = 59
            todayEnd[Calendar.SECOND] = 59
            todayEnd[Calendar.MILLISECOND] = 999
            return todayEnd.time.time
        }

        /**
         * 获取时差(时间偏移量+夏令时差)
         *
         * @param calendar [Calendar]
         * @return 时差(时间偏移量 + 夏令时差)
         */
        fun getTimeOffset(calendar: Calendar?): Int {
            var calendar = calendar
            if (calendar == null) {
                calendar = Calendar.getInstance()
            }
            val zoneOffset = calendar!![Calendar.ZONE_OFFSET]
            val dstOffset = calendar[Calendar.DST_OFFSET]
            return zoneOffset + dstOffset
        }

        /**
         * getUTCTimeFromGMT
         *
         * @param date Date
         * @return
         */
        fun getUTCTimeFromGMT(date: Date): Date? {
            return Date(date.time - getTimeOffset(Calendar.getInstance()))
        }

        /**
         * getUTCTimeFromGMT
         *
         * @param date Date
         * @return
         */
        fun getGMTTimeFromUTC(date: Date): Date? {
            return Date(date.time + getTimeOffset(Calendar.getInstance()))
        }
    }
}