package fqf.qua_mario.powerups.forms;

import fqf.qua_mario.ModMarioQuaMario;
import fqf.qua_mario.powerups.PowerUp;
import net.minecraft.util.Identifier;

public class FireForm extends PowerUp {
	public static final FireForm INSTANCE = new FireForm();
	private FireForm() {
		PowerUp.CHILDREN.add(this);
		this.ID = Identifier.of(ModMarioQuaMario.MOD_ID, "fire_form");
		this.prefix = "Fire ";
	}
}
