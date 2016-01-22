package ca.alexland.renewpass.interfaces;

/**
 * Created by Trevor on 1/21/2016.
 */
public interface IPreference {
    interface PreferenceSelectedListener {
        void onPreferenceSelected();
    }
    void setPreferenceSelectedListener(PreferenceSelectedListener preferenceSelectedListener);
}
