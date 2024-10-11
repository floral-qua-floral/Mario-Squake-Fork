package fqf.qua_mario;

import com.tom.cpm.api.ICPMPlugin;
import com.tom.cpm.api.IClientAPI;
import com.tom.cpm.api.ICommonAPI;

public class ModCPMIntegration implements ICPMPlugin {
	@Override public String getOwnerModId() {
		return ModMarioQuaMario.MOD_ID;
	}

	public static IClientAPI clientAPI;
	public static ICommonAPI commonAPI;

	@Override
	public void initClient(IClientAPI api) {
		clientAPI = api;
	}

	@Override
	public void initCommon(ICommonAPI api) {
		commonAPI = api;
	}
}
