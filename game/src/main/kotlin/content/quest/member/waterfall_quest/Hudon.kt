package content.quest.member.waterfall_quest

import content.entity.player.dialogue.Happy
import content.entity.player.dialogue.Idle
import content.entity.player.dialogue.Confused
import content.entity.player.dialogue.type.npc
import content.entity.player.dialogue.type.player
import content.quest.quest
import content.quest.refreshQuestJournal
import world.gregs.voidps.engine.Script
import world.gregs.voidps.engine.entity.character.player.Player

class Hudon : Script {

    init {
        npcOperate("Talk-to", "hudon_baxtorian_falls") {
            when {
                !get("waterfall_quest_hudon_found", false) && quest("waterfall_quest") == "started" -> meetHudon()
                quest("waterfall_quest") == "completed" -> afterQuest()
                else -> exploringHudon()
            }
        }
    }

    suspend fun Player.meetHudon() {
        npc<Happy>("Isn't this place amazing? Look at the waterfall!")
        player<Confused>("Your mother is worried sick about you.")
        npc<Idle>("Mum worries too much. I'm perfectly fine out here.")
        player<Idle>("She asked me to check on you. I'll let her know you're safe.")
        npc<Happy>("Tell her I'll be back before dark. I just want to explore a bit more!")
        set("waterfall_quest_hudon_found", true)
        refreshQuestJournal()
    }

    suspend fun Player.exploringHudon() {
        npc<Happy>("Isn't this waterfall incredible?")
        player<Idle>("It certainly is impressive.")
        npc<Idle>("I could watch it all day. Don't worry, I'll head home soon.")
    }

    suspend fun Player.afterQuest() {
        npc<Happy>("Hello again! Mum told me what you did.")
        player<Happy>("Just making sure you were safe.")
        npc<Idle>("She was quite relieved. I think I'd better not wander off so far next time!")
    }
}
