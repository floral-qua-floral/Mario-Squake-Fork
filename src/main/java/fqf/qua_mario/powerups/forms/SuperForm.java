package fqf.qua_mario.powerups.forms;

import fqf.qua_mario.ModMarioQuaMario;
import fqf.qua_mario.powerups.PowerUp;
import net.minecraft.util.Identifier;

public class SuperForm extends PowerUp {
	public static final SuperForm INSTANCE = new SuperForm();
	private SuperForm() {
		PowerUp.CHILDREN.add(this);
		this.ID = Identifier.of(ModMarioQuaMario.MOD_ID, "super_form");
		this.prefix = "Super ";
	}
}
