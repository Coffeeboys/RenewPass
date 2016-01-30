/**
 * <pre>
 * Copyright 2015 Soulwolf Ching
 * Copyright 2015 The Android Open Source Project for MaterialRadioGroup
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </pre>
 */
package net.soulwolf.widget.materialradio.listener;

import net.soulwolf.widget.materialradio.MaterialRadioGroup;

/**
 * <p>Interface definition for a callback to be invoked when the checked
 * radio button changed in this group.</p>
 *
 * author: Soulwolf Created on 2015/7/16 20:56.
 * email : Ching.Soulwolf@gmail.com
 */
public interface OnCheckedChangeListener {

    /**
     * <p>Called when the checked radio button has changed. When the
     * selection is cleared, checkedId is -1.</p>
     *
     * @param group the group in which the checked radio button has changed
     * @param checkedId the unique identifier of the newly checked radio button
     */
    public void onCheckedChanged(MaterialRadioGroup group, int checkedId);
}
