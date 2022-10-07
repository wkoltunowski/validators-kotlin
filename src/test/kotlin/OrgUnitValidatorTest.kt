import ValidationMessage.Companion.validationError
import org.junit.jupiter.api.Assertions.assertIterableEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.function.Consumer

internal class OrgUnitValidatorTest {

    private var table = Table(listOf())

    private fun validateRows(vararg rows: Row) {
        table = Table(listOf(OrgUnitValidator()))
        table.addRows(*rows)
        table.validateTable()
    }

    @Test
    fun shouldDetectDuplicateCodes() {
        validateRows(
            row(from("2018-01-01"), to("2018-01-31"), company("X")),
            row(from("2018-01-10"), to("2018-02-01"), company("X"))
        )
        assertIterableEquals(rowValidationResults(0), listOf(validationError("msg.overlapping.codes")))
        assertIterableEquals(rowValidationResults(1), listOf(validationError("msg.overlapping.codes")))
    }

    @Test
    fun shouldNotDetectWhenDifferentCodes() {
        validateRows(
            row(from("2018-01-01"), to("2018-01-31"), company("X")),
            row(from("2018-01-10"), to("2018-02-01"), company("X"))
        )
        assertTrue(rowValidationResults(0).isEmpty())
        assertTrue(rowValidationResults(1).isEmpty())
    }

    @Test
    fun shouldNotDetectWhenNoOverlap() {
        validateRows(
            row(from("2018-01-01"), to("2018-01-10"), company("X")),
            row(from("2018-01-11"), to("2018-01-31"), company("X"))
        )
        assertTrue(rowValidationResults(0).isEmpty())
        assertTrue(rowValidationResults(1).isEmpty())
    }

    private fun company(companyCode: String?) =
        Consumer { m: MutableMap<String, Any> ->
            companyCode?.let { m["company"] = it } ?: let { m.remove("company") }
        }

    private fun rowValidationResults(i: Int) = table.rowAt(i).validationResults()

    private fun from(from: String) =
        Consumer { attributes: MutableMap<String, Any> ->
            date(from)?.let { attributes["from"] = it } ?: let { attributes.remove("from") }
        }

    private fun to(to: String?) = Consumer { attributes: MutableMap<String, Any> ->
        date(to)?.let { attributes["to"] = it } ?: let { attributes.remove("to") }
    }

    private fun date(day: String?) = day?.let { LocalDate.parse(it) }

    private fun row(vararg consumers: Consumer<MutableMap<String, Any>>): Row {
        val attributesMap = mutableMapOf<String, Any>()
        consumers.forEach { it.accept(attributesMap) }
        return Row(attributesMap)
    }
}