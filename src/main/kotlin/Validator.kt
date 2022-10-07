interface RowValidator {
    fun validate(rows: List<Row>)
}

class Table(private val validators: List<RowValidator>) {

    private val rows = mutableListOf<Row>()

    fun validateTable() {
        rows.onEach { it.cleanValidation() }
        validators.onEach { it.validate(rows) }
    }

    fun rowAt(i: Int): Row = rows[i]
    fun addRows(vararg row: Row) {
        rows += row
    }
}

data class ValidationMessage(private val msg: String) {

    companion object {
        fun validationError(msg: String) = ValidationMessage(msg)
    }
}

class Row(attributes: Map<String, Any>) {
    private val attributes = mutableMapOf<String, Any>()
    private var validationResults = setOf<ValidationMessage>()

    init {
        this.attributes += attributes
    }

    fun validationResults(): Set<ValidationMessage> = validationResults

    fun addValidationMessage(validationMessage: ValidationMessage) {
        validationResults += validationMessage
    }

    fun <T> readAs(property: String): T? = attributes[property] as T

    fun cleanValidation() {
        validationResults = setOf()
    }
}