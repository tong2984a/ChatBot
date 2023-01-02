package com.codepalace.chatbot.utils

import com.codepalace.chatbot.utils.Constants.OPEN_GOOGLE
import com.codepalace.chatbot.utils.Constants.OPEN_SEARCH
import com.codepalace.chatbot.utils.Constants.SPEAK_MAID
import com.codepalace.chatbot.utils.Constants.SPEAK_MARRY
import com.codepalace.chatbot.utils.Constants.SPEAK_MEET
import com.codepalace.chatbot.utils.Constants.SPEAK_NG_INLAW_MARRY
import com.codepalace.chatbot.utils.Constants.SPEAK_NG_INLAW_MEET
import com.codepalace.chatbot.utils.Constants.SPEAK_NG_MAID
import com.codepalace.chatbot.utils.Constants.SPEAK_NG_MARRY
import com.codepalace.chatbot.utils.Constants.SPEAK_NG_STICKY_RICE_WINE
import com.codepalace.chatbot.utils.Constants.SPEAK_WHAT
import com.codepalace.chatbot.utils.Constants.SPEAK_WHERE
import com.codepalace.chatbot.utils.Constants.SPEAK_WHOM
import com.codepalace.chatbot.utils.Constants.SPEAK_PO_CEMETERY
import com.codepalace.chatbot.utils.Constants.SPEAK_PO_DESSERT
import com.codepalace.chatbot.utils.Constants.SPEAK_PO_DESSERT_SHORT
import com.codepalace.chatbot.utils.Constants.SPEAK_PO_FARM
import com.codepalace.chatbot.utils.Constants.SPEAK_PO_FARM_SHORT
import com.codepalace.chatbot.utils.Constants.SPEAK_PO_MOM
import com.codepalace.chatbot.utils.Constants.SPEAK_PO_MOM_SHORT
import com.codepalace.chatbot.utils.Constants.SPEAK_PO_PIG
import com.codepalace.chatbot.utils.Constants.SPEAK_PO_PIG_SHORT
import com.codepalace.chatbot.utils.Constants.SPEAK_WEDDING
import java.sql.Date
import java.sql.Timestamp
import java.text.SimpleDateFormat

object BotResponse {

    fun basicResponses(_message: String): String {

        val random = (0..2).random()
        val message =_message.toLowerCase()

        return when {

            //Flips a coin
            message.contains("flip") && message.contains("coin") -> {
                val r = (0..1).random()
                val result = if (r == 0) "heads" else "tails"

                "I flipped a coin and it landed on $result"
            }

            //Math calculations
            message.contains("solve") -> {
                val equation: String? = message.substringAfterLast("solve")
                return try {
                    val answer = SolveMath.solveMath(equation ?: "0")
                    "$answer"

                } catch (e: Exception) {
                    "Sorry, I can't solve that."
                }
            }

            //Speak Dessert
            message.contains("ng") && message.contains("inlaw marry")-> {
                SPEAK_NG_INLAW_MARRY
            }

            //Speak Farm
            message.contains("ng") && message.contains("inlaw meet")-> {
                SPEAK_NG_INLAW_MEET
            }

            //Speak Great Grandma
            message.contains("ng") && message.contains("maid")-> {
                SPEAK_NG_MAID
            }

            //Speak Pig
            message.contains("ng") && message.contains("marry")-> {
                SPEAK_NG_MARRY
            }

            //Speak Wine
            message.contains("ng") && message.contains("wine")-> {
                SPEAK_NG_STICKY_RICE_WINE
            }

            //Speak Cemetery
            message.contains("po") && message.contains("cemetery")-> {
                SPEAK_PO_CEMETERY
            }

            //Speak Dessert Short
            message.contains("po") && message.contains("dessert") && message.contains("full")-> {
                SPEAK_PO_DESSERT
            }

            //Speak Farm Short
            message.contains("po") && message.contains("farm") && message.contains("full")-> {
                SPEAK_PO_FARM
            }

            //Speak Great Grandma Short
            message.contains("po") && message.contains("mom") && message.contains("full")-> {
                SPEAK_PO_MOM
            }

            //Speak Pig Short
            message.contains("po") && message.contains("pig") && message.contains("full")-> {
                SPEAK_PO_PIG
            }

            //Speak Dessert Short
            message.contains("po") && message.contains("dessert")-> {
                SPEAK_PO_DESSERT_SHORT
            }

            //Speak Farm Short
            message.contains("po") && message.contains("farm")-> {
                SPEAK_PO_FARM_SHORT
            }

            //Speak Great Grandma Short
            message.contains("po") && message.contains("mom")-> {
                SPEAK_PO_MOM_SHORT
            }

            //Speak Pig Short
            message.contains("po") && message.contains("pig")-> {
                SPEAK_PO_PIG_SHORT
            }

            //Speak Where
            message.contains("speak") && message.contains("where")-> {
                SPEAK_WHERE
            }

            //Speak Whom
            message.contains("speak") && message.contains("whom")-> {
                SPEAK_WHOM
            }

            //Speak What
            message.contains("speak") && message.contains("what")-> {
                SPEAK_WHAT
            }

            //Speak Maid
            message.contains("speak") && message.contains("maid")-> {
                SPEAK_MAID
            }

            //Speak Marry
            message.contains("speak") && message.contains("marry")-> {
                SPEAK_MARRY
            }

            //Speak Meet
            message.contains("speak") && message.contains("meet")-> {
                SPEAK_MEET
            }

            //Speak Wedding
            message.contains("speak") && message.contains("wedding")-> {
                SPEAK_WEDDING
            }

            //Speak Dessert
            message.contains("help")-> {
                "po cemetery\n" +
                        "po dessert\n" +
                        "po farm\n" +
                        "po mom\n" +
                        "po pig\n" +
                        "ng inlaw marry\n" +
                        "ng inlaw meet\n" +
                        "ng maid\n" +
                        "ng marry\n" +
                        "ng wine\n" +
                        "speak where\n" +
                        "speak whom\n" +
                        "speak what"
            }

            //Where do you live?
            message.contains("where") && message.contains("live") -> {
                when (random) {
                    0 -> "I live in a village house in Shatin, Hong Kong."
                    1 -> "我住係香港沙田一間村屋個度"
                    2 -> "I reside in a village house in Shatin, Hong Kong!"
                    else -> "error" }
            }

            //Whom do you live with?
            message.contains("whom") && message.contains("live") -> {
                when (random) {
                    0 -> "我同大仔，細仔，新抱同埋孫仔孫女住架"
                    1 -> "I live with the eldest son, the younger son, and the new grandson and granddaughter"
                    2 -> "I share a home with my oldest son, younger son, and two new grandchildren."
                    3 -> "Living with eldest son, second son, new grandchild, granddaughter"
                    else -> "error" }
            }

            //What do you do to spend time?
            message.contains("what") && message.contains("spend") && message.contains("time") -> {
                when (random) {
                    0 -> "我平時同佢地食下飯，打下麻雀，傾計，睇電視架"
                    1 -> "Typically, I have meals with my family, engage in conversation, play mahjong, and watch TV."
                    2 -> "I usually eat with my family, play mahjong, chat, and watch TV."
                    else -> "error" }
            }

            //Hello
            message.contains("hello") -> {
                when (random) {
                    0 -> "Hello there!"
                    1 -> "Sup"
                    2 -> "Buongiorno!"
                    else -> "error" }
            }

            //How are you?
            message.contains("how are you") -> {
                when (random) {
                    0 -> "I'm doing fine, thanks!"
                    1 -> "I'm hungry..."
                    2 -> "Pretty good! How about you?"
                    else -> "error"
                }
            }

            //What time is it?
            message.contains("time") && message.contains("?")-> {
                val timeStamp = Timestamp(System.currentTimeMillis())
                val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm")
                val date = sdf.format(Date(timeStamp.time))

                date.toString()
            }

            //Open Google
            message.contains("open") && message.contains("google")-> {
                OPEN_GOOGLE
            }

            //Search on the internet
            message.contains("search")-> {
                OPEN_SEARCH
            }

            //When the programme doesn't understand...
            else -> {
                when (random) {
                    0 -> "I don't understand..."
                    1 -> "Try asking me something different"
                    2 -> "Idk"
                    else -> "error"
                }
            }
        }
    }
}