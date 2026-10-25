package wpics.weather.model.weather

enum class UnitSystem(
    val queryParam: String,
    val displayName: String
) {
    METRIC("metric", "Metric (°C, m/s)"),
    IMPERIAL("imperial", "Imperial (°F, mph)"),
    STANDARD("standard", "Standard (K, m/s)")
}