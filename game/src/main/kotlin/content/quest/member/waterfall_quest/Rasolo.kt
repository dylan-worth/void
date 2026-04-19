package content.quest.member.waterfall_quest

import content.entity.player.dialogue.Happy
import content.entity.player.dialogue.Idle
import content.entity.player.dialogue.Quiz
import content.entity.player.dialogue.type.choice
import content.entity.player.dialogue.type.npc
import content.entity.player.dialogue.type.player
import world.gregs.voidps.engine.Script

class Rasolo : Script {

    init {
        npcOperate("Talk-to", "rasolo_baxtorian_falls") {
            npc<Idle>("Good day, traveller. Can I interest you in some of my wares?")
            choice {
                option<Quiz>("What are you doing all the way out here?") {
                    npc<Idle>("I wander these lands buying and selling curiosities. There is much of interest in this valley.")
                    npc<Idle>("The legend of Baxtorian and his wife Glarial is particularly fascinating. I have spent years collecting artefacts related to their story.")
                }
                option<Happy>("You seem to know a lot about this area.") {
                    npc<Idle>("I have travelled extensively and spoken with many folk. There is a gnome dungeon not far from the Tree Gnome Village that is said to hold secrets of its own.")
                }
                option<Idle>("No thank you, not right now.") {
                    player<Idle>("Perhaps another time.")
                    npc<Idle>("As you wish.")
                }
            }
        }
    }
}
