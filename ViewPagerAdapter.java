package fr.ensisa.mathsmind;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity)
    {
        super(fragmentActivity);
    }
    public static final String[] nameTab = {"Invitation", "Inviter", "Parties en cours"};
    @NonNull
    @Override
    public Fragment createFragment(int position) {

       switch (position) {
            case 0:
                return  new OnlineInvitationFragment();
            case 1:
                return  new OnlineInviteFragment();
            default:
                return  new OnGoingGameFragment();
        }
    }
    @Override
    public int getItemCount() {return 3; }
}
