package com.example.eva

data class Specialist(val id: Int, val name: String)

object fakeapi {
    val specializations = listOf(
        "Акушер-гинеколог",
        "Кардиолог, Терапевт",
        "Педиатр",
        "Невролог",
        "Офтальмолог"
    )

    val branches = listOf(
        "E-Health на Университетской",
        "E-Health на Труда",
        "E-Health в Златоусте",
        "E-Health в Копейске",
        "E-Health на Комсомольском"
    )

    val specialists = listOf(
        Specialist(1, "Абдрафиков Тагир Надирович"),
        Specialist(2, "Абрамова Наталья Николаевна"),
        Specialist(3, "Авдеева Сергеев"),
        Specialist(3, "Смольников Никита Сергеевич"),
        Specialist(4, "Малаев Илья Александрович"),
        Specialist(5, "Шкляр Петр Львович"),
        Specialist(6, "Смирнов Никита Артемович")
    )
}