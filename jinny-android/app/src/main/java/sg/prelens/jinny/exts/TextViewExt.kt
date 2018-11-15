package sg.prelens.jinny.exts

import android.widget.TextView
import android.text.style.URLSpan
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.UnderlineSpan
import sg.prelens.jinny.utils.URLSpanNoUnderline


fun TextView.removeUnderline() {
    val s = SpannableString(this.text)
    val spans = s.getSpans(0, s.length, URLSpan::class.java)
    for (span in spans) {
        val start = s.getSpanStart(span)
        val end = s.getSpanEnd(span)
        s.removeSpan(span)
        val modifiedSpan = URLSpanNoUnderline(span.url)
        s.setSpan(modifiedSpan, start, end, 0)
    }
    this.text = s
}

fun TextView.removeUnderline(part: String) {
    val builder = SpannableStringBuilder(this.text)
    builder.setSpan(UnderlineSpan(), this.text.indexOf(part), this.text.indexOf(part) + part.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    this.text = builder
}

private fun underLineText(full: String, part: String): SpannableStringBuilder {
    val builder = SpannableStringBuilder(full)
    builder.setSpan(UnderlineSpan(), full.indexOf(part), full.indexOf(part) + part.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    return builder
}
