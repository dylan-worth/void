package content.quest.member.waterfall_quest

import content.entity.player.dialogue.Confused
import content.entity.player.dialogue.Disheartened
import content.entity.player.dialogue.Happy
import content.entity.player.dialogue.Idle
import content.entity.player.dialogue.Sad
import content.entity.player.dialogue.type.choice
import content.entity.player.dialogue.type.npc
import content.entity.player.dialogue.type.player
import content.quest.quest
import content.quest.refreshQuestJournal
import world.gregs.voidps.engine.Script
import world.gregs.voidps.engine.entity.character.player.Player

class Almera : Script {

    init {
        npcOperate("Talk-to", "almera_baxtorian_falls") {
            when (quest("waterfall_quest")) {
                "unstarted" -> startQuest()
                "started" -> remindBookcase()
                "found_book" -> foundBook()
                "has_pebble" -> findingTombItems()
                "completed" -> questDone()
                else -> remindBookcase()
            }
        }
    }

    suspend fun Player.startQuest() {
        npc<Sad>("Oh dear, oh dear. I do hope he's alright.")
        player<Confused>("Is something wrong?")
        npc<Sad>("My son, Hudon. He went off to explore the waterfall and I haven't seen him since. I'm worried he might have hurt himself.")
        choice {
            option<Happy>("I'll go and check on him for you.") {
                npc<Happy>("Oh, would you? That's very kind of you.")
                npc<Idle>("He was heading towards the waterfall itself. Please make sure he's safe.")
                npc<Idle>("If you get a chance, have a look around the house too. My late husband collected all manner of books about the history of this valley. Perhaps you'll find something useful.")
                set("waterfall_quest", "started")
                refreshQuestJournal()
            }
            option<Idle>("I'm sure he'll be fine.") {
                npc<Disheartened>("I hope so... I really do hope so.")
            }
        }
    }

    suspend fun Player.remindBookcase() {
        npc<Disheartened>("Have you found Hudon yet?")
        if (!get("waterfall_quest_hudon_found", false)) {
            player<Idle>("Not yet, I'm looking for him.")
            npc<Sad>("He went towards the waterfall. There's a raft just down the bank you could use to reach him.")
        } else {
            player<Happy>("Yes, he's fine! Just exploring by the waterfall.")
            npc<Happy>("Oh, thank goodness. Did you find anything useful in the house?")
            player<Idle>("I'm still searching.")
            npc<Idle>("Don't forget the bookcase in the back room. My husband collected many books about Baxtorian and this valley.")
        }
    }

    suspend fun Player.foundBook() {
        npc<Idle>("How is Hudon? Is he safe?")
        player<Happy>("He seems perfectly fine, just exploring.")
        npc<Happy>("Oh, thank goodness!")
        player<Idle>("I found an old book about Baxtorian in your house.")
        npc<Idle>("Ah, that old thing. Rasolo, the travelling merchant down by the river, was asking about that book not long ago. Perhaps he knows something about it.")
    }

    suspend fun Player.findingTombItems() {
        npc<Happy>("You do look capable. Any luck finding out about the waterfall?")
        player<Idle>("I'm still investigating.")
        npc<Idle>("Do take care out there. The waterfall is no place for the careless.")
    }

    suspend fun Player.questDone() {
        npc<Happy>("You look well, adventurer!")
        player<Happy>("It's a beautiful valley you live in.")
        npc<Idle>("It is. And thanks to you, I feel the spirits of Baxtorian and Glarial are finally at rest. You have my deepest thanks.")
    }
}
