import ValidationMessage.Companion.validationError
import java.time.LocalDate

class OrgUnitValidator : RowValidator {
    override fun validate(rows: List<Row>) {
        for (row in rows) {
            if (row.readAs<String>("code") == null) {
                row.addValidationMessage(validationError("msg.empty.code"))
            }
            if (row.readAs<String>("company") == null) {
                row.addValidationMessage(validationError("msg.empty.company"))
            }
        }
        for (row in rows) {
            for (another in rows) {
                if (from(row) != null && to(row) != null && from(row)!!.isAfter(to(row))) {
                    row.addValidationMessage(validationError("msg.invalid.interval"))
                } else if (row !== another && row.readAs<String>("code") != null && row.readAs<String>("code")
                        .equals(another.readAs("code")) && row.readAs<String>("company") != null && row.readAs<String>("company")
                        .equals(another.readAs("company")) &&
                    overlap(row, another)
                ) {
                    another.addValidationMessage(validationError("msg.overlapping.codes"))
                }
            }
        }
    }

    private fun overlap(row: Row, another: Row): Boolean {
        val first = min(row, another)
        val second = max(row, another)
        return from(second) == null || to(first) == null || from(second)!! <= to(first)!!
    }

    private fun min(row: Row, another: Row) = listOf(row, another).minBy { t -> from(t)!! }
    private fun max(row: Row, another: Row) = listOf(row, another).maxBy { t -> from(t)!! }

    private fun from(row: Row): LocalDate? = row.readAs("from")
    private fun to(row: Row): LocalDate? = row.readAs("to")

}