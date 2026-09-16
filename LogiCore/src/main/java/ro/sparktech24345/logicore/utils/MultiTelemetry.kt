package ro.sparktech24345.logicore.utils

import org.firstinspires.ftc.robotcore.external.Func
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.robotcore.external.Telemetry.DisplayFormat
import org.firstinspires.ftc.robotcore.external.Telemetry.Log.DisplayOrder

/** Delegate for a list of multiple telemetry objects.  */
open class MultiTelemetry(vararg telemetryList: Telemetry) : Telemetry {
    private val telemetryList: MutableList<Telemetry> = ArrayList(listOf(*telemetryList))
    private val log: MultipleLog

    init {
        this.log = MultipleLog()
        for (telemetry in telemetryList) {
            this.log.addLog(telemetry.log())
        }
    }

    /**
     * Adds another telemetry object.
     * 
     * @param telemetry delegate to add
     */
    fun addTelemetry(telemetry: Telemetry) {
        this.telemetryList.add(telemetry)
        this.log.addLog(telemetry.log())
    }

    override fun addData(s: String?, s1: String?, vararg objects: Any?): Telemetry.Item {
        val items: MutableList<Telemetry.Item> = ArrayList()
        for (telemetry in telemetryList) {
            items.add(telemetry.addData(s, s1, *objects))
        }
        return MultipleItem(items)
    }

    override fun addData(s: String?, o: Any?): Telemetry.Item {
        val items: MutableList<Telemetry.Item> = ArrayList()
        for (telemetry in telemetryList) {
            items.add(telemetry.addData(s, o))
        }
        return MultipleItem(items)
    }

    override fun <T> addData(s: String?, func: Func<T?>?): Telemetry.Item {
        val items: MutableList<Telemetry.Item> = ArrayList()
        for (telemetry in telemetryList) {
            items.add(telemetry.addData<T?>(s, func))
        }
        return MultipleItem(items)
    }

    override fun <T> addData(s: String?, s1: String?, func: Func<T?>?): Telemetry.Item {
        val items: MutableList<Telemetry.Item> = ArrayList()
        for (telemetry in telemetryList) {
            items.add(telemetry.addData<T?>(s, s1, func))
        }
        return MultipleItem(items)
    }

    override fun removeItem(item: Telemetry.Item?): Boolean {
        var retVal = true
        for (telemetry in telemetryList) {
            val temp = telemetry.removeItem(item)
            retVal = retVal && temp
        }
        return retVal
    }

    override fun clear() {
        for (telemetry in telemetryList) {
            telemetry.clear()
        }
    }

    override fun clearAll() {
        for (telemetry in telemetryList) {
            telemetry.clearAll()
        }
    }

    override fun addAction(runnable: Runnable?): Any? {
        for (telemetry in telemetryList) {
            telemetry.addAction(runnable)
        }
        // note: this behavior is correct given the current default Telemetry implementation
        return runnable
    }

    override fun removeAction(o: Any?): Boolean {
        var retVal = true
        for (telemetry in telemetryList) {
            val temp = telemetry.removeAction(o)
            retVal = retVal && temp
        }
        return retVal
    }

    override fun speak(text: String?) {
        for (telemetry in telemetryList) {
            telemetry.speak(text)
        }
    }

    override fun speak(text: String?, languageCode: String?, countryCode: String?) {
        for (telemetry in telemetryList) {
            telemetry.speak(text, languageCode, countryCode)
        }
    }

    override fun update(): Boolean {
        var retVal = true
        for (telemetry in telemetryList) {
            val temp = telemetry.update()
            retVal = retVal && temp
        }
        return retVal
    }

    override fun addLine(): Telemetry.Line {
        val lines: MutableList<Telemetry.Line> = ArrayList()
        for (telemetry in telemetryList) {
            lines.add(telemetry.addLine())
        }
        return MultipleLine(lines)
    }

    override fun addLine(s: String?): Telemetry.Line {
        val lines: MutableList<Telemetry.Line> = ArrayList()
        for (telemetry in telemetryList) {
            lines.add(telemetry.addLine(s))
        }
        return MultipleLine(lines)
    }

    override fun removeLine(line: Telemetry.Line?): Boolean {
        var retVal = true
        for (telemetry in telemetryList) {
            val temp = telemetry.removeLine(line)
            retVal = retVal && temp
        }
        return retVal
    }

    override fun isAutoClear(): Boolean {
        return if (telemetryList.isEmpty()) true else telemetryList[0].isAutoClear
    }

    override fun setAutoClear(b: Boolean) {
        for (telemetry in telemetryList) {
            telemetry.isAutoClear = b
        }
    }

    override fun getMsTransmissionInterval(): Int {
        return if (telemetryList.isEmpty()) 250 else telemetryList[0].msTransmissionInterval
    }

    override fun setMsTransmissionInterval(i: Int) {
        for (telemetry in telemetryList) {
            telemetry.msTransmissionInterval = i
        }
    }

    override fun getItemSeparator(): String? {
        return if (telemetryList.isEmpty()) " | " else telemetryList[0].itemSeparator
    }

    override fun setItemSeparator(s: String?) {
        for (telemetry in telemetryList) {
            telemetry.itemSeparator = s
        }
    }

    override fun getCaptionValueSeparator(): String? {
        return if (telemetryList.isEmpty()) " : " else telemetryList[0].captionValueSeparator
    }

    override fun setCaptionValueSeparator(s: String?) {
        for (telemetry in telemetryList) {
            telemetry.captionValueSeparator = s
        }
    }

    override fun setDisplayFormat(displayFormat: DisplayFormat?) {
        for (telemetry in telemetryList) {
            telemetry.setDisplayFormat(displayFormat)
        }
    }

    override fun log(): Telemetry.Log {
        return log
    }

    inner class MultipleItem(private val items: MutableList<Telemetry.Item>) : Telemetry.Item {
        override fun getCaption(): String? {
            return if (items.isEmpty()) "" else this.items[0].caption
        }

        override fun setCaption(s: String?): Telemetry.Item {
            for (item in items) {
                item.caption = s
            }
            return this
        }

        override fun setValue(s: String?, vararg objects: Any?): Telemetry.Item {
            for (item in items) {
                item.setValue(s, *objects)
            }
            return this
        }

        override fun setValue(o: Any?): Telemetry.Item {
            for (item in items) {
                item.setValue(o)
            }
            return this
        }

        override fun <T> setValue(func: Func<T?>?): Telemetry.Item {
            for (item in items) {
                item.setValue<T?>(func)
            }
            return this
        }

        override fun <T> setValue(s: String?, func: Func<T?>?): Telemetry.Item {
            for (item in items) {
                item.setValue<T?>(s, func)
            }
            return this
        }

        override fun setRetained(aBoolean: Boolean?): Telemetry.Item {
            for (item in items) {
                item.setRetained(aBoolean)
            }
            return this
        }

        override fun isRetained(): Boolean {
            return if (items.isEmpty()) false else this.items[0].isRetained
        }

        override fun addData(s: String?, s1: String?, vararg objects: Any?): Telemetry.Item {
            for (item in items) {
                item.addData(s, s1, *objects)
            }
            return this
        }

        override fun addData(s: String?, o: Any?): Telemetry.Item {
            for (item in items) {
                item.addData(s, o)
            }
            return this
        }

        override fun <T> addData(s: String?, func: Func<T?>?): Telemetry.Item {
            for (item in items) {
                item.addData<T?>(s, func)
            }
            return this
        }

        override fun <T> addData(s: String?, s1: String?, func: Func<T?>?): Telemetry.Item {
            for (item in items) {
                item.addData<T?>(s, s1, func)
            }
            return this
        }
    }

    inner class MultipleLine(private val lines: MutableList<Telemetry.Line>) : Telemetry.Line {
        override fun addData(s: String?, s1: String?, vararg objects: Any?): Telemetry.Item {
            val items: MutableList<Telemetry.Item> = ArrayList()
            for (line in lines) {
                items.add(line.addData(s, s1, *objects))
            }
            return MultipleItem(items)
        }

        override fun addData(s: String?, o: Any?): Telemetry.Item {
            val items: MutableList<Telemetry.Item> = ArrayList()
            for (line in lines) {
                items.add(line.addData(s, o))
            }
            return MultipleItem(items)
        }

        override fun <T> addData(s: String?, func: Func<T?>?): Telemetry.Item {
            val items: MutableList<Telemetry.Item> = ArrayList()
            for (line in lines) {
                items.add(line.addData<T?>(s, func))
            }
            return MultipleItem(items)
        }

        override fun <T> addData(s: String?, s1: String?, func: Func<T?>?): Telemetry.Item {
            val items: MutableList<Telemetry.Item> = ArrayList()
            for (line in lines) {
                items.add(line.addData<T?>(s, s1, func))
            }
            return MultipleItem(items)
        }
    }

    inner class MultipleLog : Telemetry.Log {
        private val logs: MutableList<Telemetry.Log> = ArrayList()

        fun addLog(log: Telemetry.Log?) {
            this.logs.add(log!!)
        }

        override fun getCapacity(): Int {
            return if (logs.isEmpty()) 0 else logs[0].capacity
        }

        override fun setCapacity(i: Int) {
            for (log in logs) {
                log.capacity = i
            }
        }

        override fun getDisplayOrder(): DisplayOrder? {
            return if (logs.isEmpty()) DisplayOrder.OLDEST_FIRST else logs[0].displayOrder
        }

        override fun setDisplayOrder(displayOrder: DisplayOrder?) {
            for (log in logs) {
                log.displayOrder = displayOrder
            }
        }

        override fun add(s: String?) {
            for (log in logs) {
                log.add(s)
            }
        }

        override fun add(s: String?, vararg objects: Any?) {
            for (log in logs) {
                log.add(s, *objects)
            }
        }

        override fun clear() {
            for (log in logs) {
                log.clear()
            }
        }
    }
}