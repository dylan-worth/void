package content.area.karamja.tai_bwo_wannai

import content.entity.player.dialogue.Happy
import content.entity.player.dialogue.Idle
import content.entity.player.dialogue.Quiz
import content.entity.player.dialogue.Sad
import content.entity.player.dialogue.type.choice
import content.entity.player.dialogue.type.npc
import content.entity.player.dialogue.type.player
import content.entity.player.dialogue.type.startQuest
import content.quest.quest
import content.quest.questComplete
import content.quest.refreshQuestJournal
import world.gregs.voidps.engine.Script
import world.gregs.voidps.engine.entity.character.jingle
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.skill.Skill
import world.gregs.voidps.engine.entity.character.player.skill.exp.exp
import world.gregs.voidps.engine.event.AuditLog
import world.gregs.voidps.engine.inv.inventory
import world.gregs.voidps.engine.inv.remove
import world.gregs.voidps.engine.queue.softQueue

class Trufitus : Script {

    init {
        npcOperate("Talk-to", "trufitus_tai_bwo_wannai") {
            when (quest("jungle_potion")) {
                "unstarted" -> unstarted()
                "started" -> snakeWeed()
                "snake_weed" -> ardrigal()
                "ardrigal" -> sitoFoil()
                "sito_foil" -> volenciaMoss()
                "volencia_moss" -> roguesPurse()
                else -> completed()
            }
        }
    }

    suspend fun Player.unstarted() {
        npc<Idle>("Ah, a visitor. Welcome to Tai Bwo Wannai.")
        player<Quiz>("Where is everyone? This place seems deserted.")
        npc<Sad>("The jungle grows ever wilder, and many of my people are too afraid to stay in the village. They have fled deeper into the trees.")
        player<Idle>("That sounds troubling. Is there anything I can do to help?")
        npc<Idle>("Perhaps. I am Trufitus, witch doctor of this tribe. I wish to commune with the spirits of our ancestors, so that I might learn how to protect my people, but I lack the ingredients for the ritual potion.")
        npc<Idle>("If you were to gather some rare jungle herbs for me, I could brew the potion and see what the spirits have to say.")
        choice {
            option<Idle>("I'll help you gather the herbs.") {
                if (startQuest("jungle_potion")) {
                    set("jungle_potion", "started")
                    refreshQuestJournal()
                    npc<Happy>("Wonderful! The first herb I require is snake weed. It grows on marshy jungle vines, along the coast to the south-west of here.")
                    player<Idle>("Snake weed, on marshy jungle vines, to the south-west. I'll find some.")
                } else {
                    player<Idle>("Sorry, I don't have time to help right now.")
                    npc<Sad>("A pity. Come back if you change your mind.")
                }
            }
            option<Idle>("Sorry, I can't help right now.") {
                npc<Sad>("A pity. Come back if you change your mind.")
            }
        }
    }

    suspend fun Player.snakeWeed() {
        when {
            inventory.contains("clean_snake_weed") -> {
                player<Idle>("I found some snake weed for you.")
                inventory.remove("clean_snake_weed")
                npc<Happy>("Excellent! This is exactly what I need.")
                npc<Idle>("Now I require ardrigal. It grows on the palm trees found on the peninsula to the north-east, but beware the harpie bug swarms that nest there.")
                set("jungle_potion", "snake_weed")
                refreshQuestJournal()
            }
            inventory.contains("grimy_snake_weed") -> {
                npc<Idle>("That snake weed is still grimy. You'll need to clean it before I can use it.")
            }
            else -> {
                npc<Idle>("I still need that snake weed. Look for marshy jungle vines along the coast, to the south-west of here.")
            }
        }
    }

    suspend fun Player.ardrigal() {
        when {
            inventory.contains("clean_ardrigal") -> {
                player<Idle>("I found some ardrigal for you.")
                inventory.remove("clean_ardrigal")
                npc<Happy>("Perfect. My mind grows clearer already.")
                npc<Idle>("Now I need sito foil. It can be found in patches of scorched earth, just south of the village.")
                set("jungle_potion", "ardrigal")
                refreshQuestJournal()
            }
            inventory.contains("grimy_ardrigal") -> {
                npc<Idle>("That ardrigal is still grimy. You'll need to clean it before I can use it.")
            }
            else -> {
                npc<Idle>("I still need that ardrigal. It grows on the palm trees on the peninsula to the north-east - watch out for the harpie bug swarms.")
            }
        }
    }

    suspend fun Player.sitoFoil() {
        when {
            inventory.contains("clean_sito_foil") -> {
                player<Idle>("I found some sito foil for you.")
                inventory.remove("clean_sito_foil")
                npc<Happy>("Good, good. Three herbs down.")
                npc<Idle>("Now I need volencia moss. Search the rocks at the mining site to the south-east of here.")
                set("jungle_potion", "sito_foil")
                refreshQuestJournal()
            }
            inventory.contains("grimy_sito_foil") -> {
                npc<Idle>("That sito foil is still grimy. You'll need to clean it before I can use it.")
            }
            else -> {
                npc<Idle>("I still need that sito foil. Search the patches of scorched earth just south of the village.")
            }
        }
    }

    suspend fun Player.volenciaMoss() {
        when {
            inventory.contains("clean_volencia_moss") -> {
                player<Idle>("I found some volencia moss for you.")
                inventory.remove("clean_volencia_moss")
                npc<Happy>("Wonderful! Just one herb left.")
                npc<Idle>("I need a rogue's purse. Search the fungus covered walls of the dungeon on the cliffs to the north-east - but watch out for jogres, they can be vicious.")
                set("jungle_potion", "volencia_moss")
                refreshQuestJournal()
            }
            inventory.contains("grimy_volencia_moss") -> {
                npc<Idle>("That volencia moss is still grimy. You'll need to clean it before I can use it.")
            }
            else -> {
                npc<Idle>("I still need that volencia moss. Search the rocks at the mining site to the south-east of here.")
            }
        }
    }

    suspend fun Player.roguesPurse() {
        when {
            inventory.contains("clean_rogues_purse") -> {
                player<Idle>("Here is the rogue's purse.")
                inventory.remove("clean_rogues_purse")
                npc<Happy>("At last, all five herbs! Now I can commune with the spirits of my ancestors.")
                npc<Idle>("Thank you, outsider. You have done my tribe a great service today.")
                completeQuest()
            }
            inventory.contains("grimy_rogues_purse") -> {
                npc<Idle>("That rogue's purse is still grimy. You'll need to clean it before I can use it.")
            }
            else -> {
                npc<Idle>("I still need that rogue's purse. Search the fungus covered walls of the dungeon on the cliffs to the north-east - but watch out for jogres.")
            }
        }
    }

    suspend fun Player.completed() {
        npc<Happy>("Thank you again for gathering those herbs for me, outsider. The spirits of my ancestors have much to say.")
        player<Idle>("I'm glad I could help.")
    }

    fun Player.completeQuest() {
        AuditLog.event(this, "quest_completed", "jungle_potion")
        set("jungle_potion", "completed")
        jingle("quest_complete_1")
        exp(Skill.Herblore, 775.0)
        refreshQuestJournal()
        inc("quest_points")
        softQueue("quest_complete", 1) {
            questComplete(
                "Jungle Potion",
                "1 Quest Point",
                "775 Herblore XP",
            )
        }
    }
}
