package fqf.qua_mario.powerups.forms;

import fqf.qua_mario.ModMarioQuaMario;
import fqf.qua_mario.powerups.PowerUp;
import net.minecraft.util.Identifier;

public class SmallForm extends PowerUp {
	public static final SmallForm INSTANCE = new SmallForm();
	private SmallForm() {
		PowerUp.CHILDREN.add(this);
		this.ID = Identifier.of(ModMarioQuaMario.MOD_ID, "small_form");
		this.prefix = "";
	}
}
