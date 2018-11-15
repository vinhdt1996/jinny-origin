package sg.prelens.jinny.utils

/**
 * Author : BIMBIM<br>.
 * Create Date : 3/22/18<br>.
 */
class Util {
    companion object {
        fun isListValid(src: List<*>?): Boolean {
            return src != null && src.size > 0
        }

        fun upperCaseAllFirst(value: String?): String {
            if (value != null) {
                val array = value!!.toCharArray()
                array[0] = Character.toUpperCase(array[0])
                for (i in 1 until array.size) {
                    if (Character.isWhitespace(array[i - 1])) {
                        array[i] = Character.toUpperCase(array[i])
                    }
                }
                return String(array)
            }
            return ""
        }
    }
}