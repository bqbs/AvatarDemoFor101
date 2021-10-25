package com.github.bqbs.hmos.avatarfor101;

import com.github.bqbs.hmos.avatarfor101.slice.DiffAbilitySlice;
import com.github.bqbs.hmos.avatarfor101.slice.DifferentShaderAbilitySlice;
import com.github.bqbs.hmos.avatarfor101.slice.MainAbilitySlice;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;

public class MainAbility extends Ability {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(MainAbilitySlice.class.getName());
        addActionRoute("action.ame", DiffAbilitySlice.class.getName());
        addActionRoute("action.diff_shader", DifferentShaderAbilitySlice.class.getName());
    }
}
