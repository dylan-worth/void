package content.quest.member.waterfall_quest

import content.entity.player.dialogue.Neutral
import content.entity.player.dialogue.type.npc
import content.entity.player.dialogue.type.player
import world.gregs.voidps.engine.Script
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.male
import world.gregs.voidps.engine.inv.carriesItem

class Hadley : Script {

    init {
        npcOperate("Talk-to", "hadley_baxtorian_falls") {
            if (carriesItem("book_on_baxtorian")) {
                hasBook()
            } else {
                intro()
            }
        }
    }

    suspend fun Player.hasBook() {
        player<Neutral>("Hello there.")
        npc<Neutral>("I hope you're enjoying your stay, there should be lots of useful information in that book: places to go, people to see.")
    }

    suspend fun Player.intro() {
        player<Neutral>("Hello there.")
        npc<Neutral>("Are you on holiday? If so you've come to the right place. I'm Hadley the tourist guide, anything you need to know just ask me. We have some of the most unspoilt wildlife and scenery in Gielinor.")
        npc<Neutral>("People come from miles around to fish in the clear lakes or to wander the beautiful hill sides.")
        player<Neutral>("It is quite pretty.")
        npc<Neutral>("Surely pretty is an understatement kind ${if (male) "Sir" else "Lady"}. Beautiful, amazing or possibly life-changing would be more suitable wording. Have you seen the Baxtorian waterfall? Named after the elf king who was buried underneath.")
        player<Neutral>("Thanks then, goodbye.")
        npc<Neutral>("Enjoy your visit.")
    }
}
