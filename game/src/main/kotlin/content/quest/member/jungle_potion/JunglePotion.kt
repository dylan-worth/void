package content.quest.member.jungle_potion

import content.quest.quest
import content.quest.questJournal
import world.gregs.voidps.engine.Script

class JunglePotion : Script {

    init {
        questJournalOpen("jungle_potion") {
            val lines = when (quest("jungle_potion")) {
                "completed" -> listOf(
                    "<str>Trufitus, the witch doctor of Tai Bwo Wannai, asked me to gather",
                    "<str>five rare jungle herbs so that he could commune with the spirits",
                    "<str>of his ancestors.",
                    "",
                    "<str>I collected snake weed, ardrigal, sito foil, volencia moss and",
                    "<str>rogue's purse and brought them all back to him.",
                    "",
                    "<red>QUEST COMPLETE!",
                )
                "started" -> listOf(
                    "<str>Trufitus, the witch doctor of Tai Bwo Wannai, asked me to gather",
                    "<str>five rare jungle herbs so that he could commune with the spirits",
                    "<str>of his ancestors.",
                    "",
                    "<navy>I should find some <maroon>snake weed<navy>, which grows on <maroon>marshy jungle",
                    "<maroon>vines <navy>along the coast, south-west of the village.",
                )
                "snake_weed" -> listOf(
                    "<str>Trufitus, the witch doctor of Tai Bwo Wannai, asked me to gather",
                    "<str>five rare jungle herbs so that he could commune with the spirits",
                    "<str>of his ancestors.",
                    "",
                    "<str>I gave Trufitus some snake weed.",
                    "",
                    "<navy>I should find some <maroon>ardrigal<navy>, which grows on <maroon>palm trees <navy>on the",
                    "<navy>peninsula to the north-east. I should watch out for harpie bug swarms.",
                )
                "ardrigal" -> listOf(
                    "<str>Trufitus, the witch doctor of Tai Bwo Wannai, asked me to gather",
                    "<str>five rare jungle herbs so that he could commune with the spirits",
                    "<str>of his ancestors.",
                    "",
                    "<str>I gave Trufitus some snake weed and ardrigal.",
                    "",
                    "<navy>I should find some <maroon>sito foil<navy>, which can be found in patches of",
                    "<maroon>scorched earth <navy>just south of the village.",
                )
                "sito_foil" -> listOf(
                    "<str>Trufitus, the witch doctor of Tai Bwo Wannai, asked me to gather",
                    "<str>five rare jungle herbs so that he could commune with the spirits",
                    "<str>of his ancestors.",
                    "",
                    "<str>I gave Trufitus some snake weed, ardrigal and sito foil.",
                    "",
                    "<navy>I should find some <maroon>volencia moss<navy>, found by searching rocks at a",
                    "<navy>mining site south-east of the village.",
                )
                "volencia_moss" -> listOf(
                    "<str>Trufitus, the witch doctor of Tai Bwo Wannai, asked me to gather",
                    "<str>five rare jungle herbs so that he could commune with the spirits",
                    "<str>of his ancestors.",
                    "",
                    "<str>I gave Trufitus some snake weed, ardrigal, sito foil and volencia moss.",
                    "",
                    "<navy>I should find a <maroon>rogue's purse<navy>, found by searching the fungus",
                    "<navy>covered walls of the dungeon to the north-east. I should watch out",
                    "<navy>for jogres.",
                )
                else -> listOf(
                    "<navy>I can start this quest by speaking to <maroon>Trufitus<navy>, in his hut",
                    "<navy>north-east of <maroon>Tai Bwo Wannai <navy>village, on <maroon>Karamja.",
                    "",
                    "<navy>I need at least <maroon>level 3 Herblore <navy>to clean the herbs he needs.",
                )
            }
            questJournal("Jungle Potion", lines)
        }
    }
}
