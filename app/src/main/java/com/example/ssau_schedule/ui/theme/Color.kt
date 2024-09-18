package com.example.ssau_schedule.ui.theme

import androidx.compose.ui.graphics.Color

class ApplicationColors {
    companion object {
        val Primary01 = Color(0xFF0D47A1)
        val Primary02 = Color(0xFF134BC5)
        val Primary03 = Color(0xFF1D5DEB)
        val Primary04 = Color(0xFF2780E3)
        val Primary05 = Color(0xFF6B92E5)
        val Primary06 = Color(0xFFA8C4EC)

        val White = Color(0xFFFFFFFF)

        val Gray01 = Color(0xFF2C2C2C)
        val Gray02 = Color(0xFF383838)
        val Gray03 = Color(0xFF66727F)
        val Gray04 = Color(0xFFCDCDCD)

        val Red01 = Color(0xFFEE3F58)
        val Red02 = Color(0xFF7E212E)
    }
}

class LessonColors {
    class Background {
        class Light {
            companion object {
                val Lecture = Color(0xFFEAF9F0)
                val Practice = Color(0xFFDFEEFF)
                val Laboratory = Color(0xFFFFE2FE)
                val Other = Color(0xFFFFF0DD)
                val Examination = Color(0xFFDAE2F4)
                val Test = Color(0xFFEAEEF2)
                val Consultation = Color(0xFFD6FAFE)

                val Unknown = Color(0xFFE2E2E2)
            }
        }
        class Dark {
            companion object {
                val Lecture = Color(0xFF444946)
                val Practice = Color(0xFF41464B)
                val Laboratory = Color(0xFF4B424A)
                val Other = Color(0xFF4B4641)
                val Examination = Color(0xFF404247)
                val Test = Color(0xFF404247)
                val Consultation = Color(0xFF3E494A)

                val Unknown = Color(0xFF444444)
            }
        }
    }

    class Foreground {
        companion object {
            val Lecture = Color(0xFF16A086)
            val Practice = Color(0xFF64B5FF)
            val Laboratory = Color(0xFFDF5FFF)
            val Other = Color(0xFFF19236)
            val Examination = Color(0xFF0B40B3)
            val Test = Color(0xFF5E7EA1)
            val Consultation = Color(0xFF0BB4BF)

            val Unknown = ApplicationColors.Gray02
        }
    }
}

