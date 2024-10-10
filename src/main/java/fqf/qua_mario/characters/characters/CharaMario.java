package fqf.qua_mario.characters.characters;

import fqf.qua_mario.ModMarioQuaMario;
import fqf.qua_mario.characters.CharaStat;
import fqf.qua_mario.characters.MarioCharacter;
import net.minecraft.util.Identifier;

import java.util.EnumMap;

public class CharaMario extends MarioCharacter {
	public static final CharaMario INSTANCE = new CharaMario();
	private CharaMario() {
		this.ID = Identifier.of(ModMarioQuaMario.MOD_ID, "mario");
		this.name = "Mario";

		this.statFactors = new EnumMap<>(CharaStat.class);
	}
}
