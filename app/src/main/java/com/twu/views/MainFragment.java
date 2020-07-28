package com.twu.views;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.slider.Slider;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.twu.R;
import com.twu.models.DataContainer;
import com.twu.models.news.News;
import com.twu.models.news.NormalNews;
import com.twu.models.news.SuperNews;
import com.twu.models.user.NormalUser;
import com.twu.models.user.User;
import com.twu.util.NavigationIconClickListener;

import java.util.List;

public class MainFragment extends Fragment {

    private HotSearchItemAdapter adapter;
    private int voteSelectedItemIndex = -1;
    private int bidSelectedItemIndex = -1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment, container, false);

        /* find view by id */
        LinearLayout backdropLinearLayout = view.findViewById(R.id.backdrop_layout);
        ListView hotSearchesListView = view.findViewById(R.id.hot_search_list_view);
        TextView emptyTextView = view.findViewById(R.id.hot_search_empty_text_view);
        /* find view by id */

        addMenuItems(view, backdropLinearLayout);
        setUpToolbar(view);

        initSomeNews();

        adapter = new HotSearchItemAdapter(getContext(), DataContainer.getInstance().getNewsList());
        hotSearchesListView.setAdapter(adapter);
        refreshHotSearches(hotSearchesListView, emptyTextView);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.toolbar_menu, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    private void setUpToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.app_bar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
        }

        toolbar.setNavigationOnClickListener(new NavigationIconClickListener(getContext(), view.findViewById(R.id.nested_scroll_view)));
    }

    private void addMenuItems(View mainView, LinearLayout backdropLinearLayout) {
        List<String> menuItemNameList = DataContainer.getInstance().getMenuItemNameList();

        for (String menuItemName : menuItemNameList) {
            MaterialButton materialButton = new MaterialButton(getContext());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            materialButton.setLayoutParams(layoutParams);
            materialButton.setText(menuItemName);

            // add click listener
            switch (menuItemName) {
                case "View":
                    materialButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            onViewButtonClicked(mainView);
                        }
                    });
                    break;

                case "Vote":
                    materialButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            onVoteButtonClicked(mainView);
                        }
                    });
                    break;

                case "Bid":
                    materialButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            onBidButtonClicked(mainView);
                        }
                    });
                    break;

                case "Add":
                    materialButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            onAddButtonClicked(mainView);
                        }
                    });
                    break;

                case "Log Out":
                    materialButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            onLogOutButtonClicked(mainView);
                        }
                    });
                    break;

                case "Add Super Hot":
                    materialButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            onAddSuperHotButtonClicked(mainView);
                        }
                    });
                    break;

                default:
                    break;
            }

            backdropLinearLayout.addView(materialButton);
        }
    }

    private void onViewButtonClicked(View mainView) {
        Toolbar toolbar = mainView.findViewById(R.id.app_bar);
        // find image button
        for (int i = 0; i < toolbar.getChildCount(); ++i) {
            View childView = toolbar.getChildAt(i);
            if (childView instanceof ImageButton) {
                ImageButton imageButton = (ImageButton) childView;
                if (imageButton.getDrawable() == toolbar.getNavigationIcon()) {
                    imageButton.performClick();
                    return;
                }
            }
        }
    }

    private void onVoteButtonClicked(View mainView) {
        onViewButtonClicked(mainView);
        showVoteHotSearchDialog(mainView);
    }

    private void showVoteHotSearchDialog(View mainView) {
        // create custom view
        View view = LayoutInflater.from(getContext()).inflate(R.layout.vote_hot_search_dialog, null, false);
        TextView voteTimesLeftTextView = view.findViewById(R.id.vote_times_left_text_view);
        RadioGroup voteItemRadioGroup = view.findViewById(R.id.vote_items_radio_group);
        Slider voteTimesSlider = view.findViewById(R.id.vote_times_slider);

        // set the first line (text view)
        User currentUser = DataContainer.getInstance().getUser();
        assert currentUser instanceof NormalUser;
        NormalUser currentNormalUser = (NormalUser) currentUser;
        voteTimesLeftTextView.setText(String.valueOf(currentNormalUser.getVoteCount()));

        // set slider
        if (currentNormalUser.getVoteCount() < 1) {
            voteTimesSlider.setValueFrom(0);
            voteTimesSlider.setValueTo(1);
        } else {
            voteTimesSlider.setValueTo(currentNormalUser.getVoteCount());
        }
        voteTimesSlider.setEnabled(false);

        int tmpIndex = 0;
        ColorStateList colorStateList = createRadioButtonColorStateList();
        List<String> allNewsContents = DataContainer.getInstance().getAllNewsContents();
        for (String newsContent : allNewsContents) {
            RadioButton radioButton = new RadioButton(getContext());
            radioButton.setText(newsContent);
            radioButton.setTag(tmpIndex++);
            radioButton.setButtonTintList(colorStateList);
            radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if (isChecked) {
                        voteSelectedItemIndex = (int) compoundButton.getTag();
                        voteTimesSlider.setEnabled(true);
                    }
                }
            });
            voteItemRadioGroup.addView(radioButton);
        }

        // create dialog
        AlertDialog voteAlertDialog = new MaterialAlertDialogBuilder(getContext())
                .setTitle(R.string.vote_hot_search_dialog_title)
                .setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .setPositiveButton(R.string.vote, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .setView(view)
                .setCancelable(false)
                .show();
        Button positiveButton = voteAlertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setEnabled(shouldVoteDialogPositiveButtonEnabled(voteTimesSlider, currentNormalUser));
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // vote
                currentNormalUser.vote(
                        getSelectedNews(voteSelectedItemIndex),
                        (int) voteTimesSlider.getValue()
                );
                DataContainer.getInstance().sortNews();
                adapter.notifyDataSetChanged();
                voteAlertDialog.dismiss();
                showSnackBar(mainView, getString(R.string.vote_hot_search_dialog_succeed));
            }
        });

        voteTimesSlider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                // can't vote 0 times
                positiveButton.setEnabled(shouldVoteDialogPositiveButtonEnabled(slider, currentNormalUser));
            }
        });
    }

    private boolean shouldVoteDialogPositiveButtonEnabled(Slider voteTimesSlider, NormalUser user) {
        return voteTimesSlider.getValue() > 0 && voteSelectedItemIndex > -1 && voteTimesSlider.getValue() <= user.getVoteCount();
    }

    private void onBidButtonClicked(View mainView) {
        onViewButtonClicked(mainView);
        showBidHotSearchDialog(mainView);
    }

    private void showBidHotSearchDialog(View mainView) {
        // create custom view
        View view = LayoutInflater.from(getContext()).inflate(R.layout.bid_hot_search_dialog, null, false);
        RadioGroup bidItemRadioGroup = view.findViewById(R.id.bid_items_radio_group);
        TextInputLayout bidPriceInputLayout = view.findViewById(R.id.bid_price_text_input);
        TextInputEditText bidPriceEditText = view.findViewById(R.id.bid_price_edit_text);
        TextView bidPriceTextView = view.findViewById(R.id.bid_price_text_view);
        Slider bidRankSlider = view.findViewById(R.id.bid_rank_slider);

        // create dialog
        AlertDialog bidAlertDialog = new MaterialAlertDialogBuilder(getContext())
                .setTitle(R.string.bid_hot_search_dialog_title)
                .setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .setPositiveButton(R.string.bid, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .setView(view)
                .setCancelable(false)
                .show();
        Button positiveButton = bidAlertDialog.getButton(DialogInterface.BUTTON_POSITIVE);

        int tmpIndex = 0;
        ColorStateList colorStateList = createRadioButtonColorStateList();
        List<String> allNewsContents = DataContainer.getInstance().getAllNewsContents();
        for (String newsContent : allNewsContents) {
            RadioButton radioButton = new RadioButton(getContext());
            radioButton.setText(newsContent);
            radioButton.setTag(tmpIndex++);
            radioButton.setButtonTintList(colorStateList);
            radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if (isChecked) {
                        bidSelectedItemIndex = (int) compoundButton.getTag();
                        bidPriceInputLayout.setEnabled(true);
                        bidRankSlider.setEnabled(true);
                        positiveButton.setEnabled(shouldBidDialogButtonEnabled(bidRankSlider));
                        bidPriceTextView.setText(String.valueOf(getSelectedNews((int) (bidRankSlider.getValue() - 1)).getPaidPrice()));
                    }
                }
            });
            bidItemRadioGroup.addView(radioButton);
        }

        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // bid
                if (!isBidPriceValid(bidPriceEditText.getText())) {
                    bidPriceInputLayout.setError(getString(R.string.bid_price_empty));
                    return;
                }
                if (isBidPriceNotEnough(bidPriceEditText.getText(), bidPriceTextView.getText().toString())) {
                    bidPriceInputLayout.setError(getString(R.string.bid_price_not_enough));
                    return;
                }
                bidPriceInputLayout.setError(null);

                User currentUser = DataContainer.getInstance().getUser();
                assert currentUser instanceof NormalUser;
                NormalUser currentNormalUser = (NormalUser) currentUser;
                currentNormalUser.bid(getSelectedNews(bidSelectedItemIndex), (int) bidRankSlider.getValue(), Integer.parseInt(bidPriceEditText.getText().toString()));
                DataContainer.getInstance().sortNews();
                adapter.notifyDataSetChanged();
                bidAlertDialog.dismiss();
                showSnackBar(mainView, getString(R.string.vote_hot_search_dialog_succeed));
            }
        });

        bidRankSlider.setValueTo(allNewsContents.size());
        bidRankSlider.setEnabled(false);
        bidRankSlider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                positiveButton.setEnabled(shouldBidDialogButtonEnabled(slider));
                bidPriceTextView.setText(String.valueOf(getSelectedNews((int) (value - 1)).getPaidPrice()));
            }
        });
    }

    private boolean shouldBidDialogButtonEnabled(Slider bidRankSlider) {
        return bidRankSlider.getValue() <= bidSelectedItemIndex;
    }

    private boolean isBidPriceNotEnough(Editable bidPriceStr, String lastBidPriceStr) {
        int bidPrice = Integer.parseInt(bidPriceStr.toString());
        int lowestPrice = Integer.parseInt(lastBidPriceStr);
        return bidPrice <= lowestPrice;
    }

    private boolean isBidPriceValid(Editable price) {
        return price != null && price.length() > 0;
    }

    private void onAddButtonClicked(View mainView) {
        onViewButtonClicked(mainView);
        showAddHotSearchDialog(mainView, false);
    }

    private void showAddHotSearchDialog(View mainView, boolean isSuperNews) {
        // create custom view
        View view = LayoutInflater.from(getContext()).inflate(R.layout.add_hot_search_dialog, null, false);
        TextInputLayout contentInputLayout = view.findViewById(R.id.hot_search_content_text_input);
        TextInputEditText contentEditText = view.findViewById(R.id.hot_search_content_edit_text);

        // create dialog
        AlertDialog addAlertDialog = new MaterialAlertDialogBuilder(getContext())
                .setTitle(isSuperNews ? R.string.add_super_hot_search_dialog_title : R.string.add_hot_search_dialog_title)
                .setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .setMessage(R.string.add_hot_search_hint)
                .setView(view)
                .setCancelable(false)
                .show();
        addAlertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // add
                // check if edit text is valid
                if (isEditTextContentValid(contentEditText.getText())) {
                    contentInputLayout.setError(null);

                    News news = isSuperNews
                            ? new SuperNews(contentEditText.getText().toString())
                            : new NormalNews(contentEditText.getText().toString());
                    DataContainer.getInstance().getUser().addNews(news);
                    DataContainer.getInstance().sortNews();
                    adapter.notifyDataSetChanged();
                    addAlertDialog.dismiss();

                    showSnackBar(mainView,
                            isSuperNews
                                    ? getString(R.string.add_super_hot_search_dialog_succeed)
                                    : getString(R.string.add_hot_search_dialog_succeed)
                    );
                } else {
                    contentInputLayout.setError(getString(R.string.error_content_empty));
                }
            }
        });
    }

    private boolean isEditTextContentValid(Editable content) {
        return content != null && content.length() > 0;
    }

    private void onLogOutButtonClicked(View mainView) {
        // onViewButtonClicked(mainView);
        getFragmentManager().popBackStack();
    }

    private void onAddSuperHotButtonClicked(View mainView) {
        onViewButtonClicked(mainView);
        showAddHotSearchDialog(mainView, true);
    }

    private ColorStateList createRadioButtonColorStateList() {
        return new ColorStateList(
                new int[][]{
                        new int[]{-android.R.attr.state_checked},
                        new int[]{android.R.attr.state_checked}
                },
                new int[]{
                        Color.BLACK,
                        ContextCompat.getColor(getContext(), R.color.colorSecondary)
                }
        );
    }

    private void refreshHotSearches(ListView hotSearchesListView, TextView emptyTextView) {
        // check empty
        List<News> newsList = DataContainer.getInstance().getNewsList();
        if (newsList.isEmpty()) {
            hotSearchesListView.setVisibility(View.GONE);
            emptyTextView.setVisibility(View.VISIBLE);
        } else {
            hotSearchesListView.setVisibility(View.VISIBLE);
            emptyTextView.setVisibility(View.GONE);
        }

        adapter.notifyDataSetChanged();
    }

    private News getSelectedNews(int index) {
        return DataContainer.getInstance().getNewsList().get(index);
    }

    private void initSomeNews() {
        if (DataContainer.getInstance().getNewsList().size() > 0) {
            return;
        }

        User user1 = DataContainer.getInstance().getUser();

        News news1 = new NormalNews("中美两国领事馆关闭");
        user1.addNews(news1);

        News news2 = new NormalNews("杭州碎尸案凶手");
        user1.addNews(news2);

        News news3 = new SuperNews("日本演员三浦春马去世");
        user1.addNews(news3);

        News news4 = new NormalNews("蓬佩奥发表演讲");
        user1.addNews(news4);

        News news5 = new NormalNews("港区国安法正式通过");
        user1.addNews(news5);

        // NormalUser user = isAdminUser ? (AdminUser)(NormalUser) user1;
        /*user.vote(news3, 4);
        user.vote(news4, 3);
        user.vote(news5, 2);*/
        // user.bid(news2, 1, 100);

        DataContainer.getInstance().sortNews();
        DataContainer.getInstance().printForDebug();
    }

    private void showSnackBar(View view, String content) {
        Snackbar.make(view, content, Snackbar.LENGTH_LONG).show();
    }

    class HotSearchItemAdapter extends BaseAdapter {
        private Context context;
        private List<News> newsList;

        public HotSearchItemAdapter(Context context, List<News> newsList) {
            this.context = context;
            this.newsList = newsList;
        }

        @Override
        public int getCount() {
            return newsList.size();
        }

        @Override
        public Object getItem(int i) {
            return newsList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            HotSearchItemViewHolder viewHolder;
            if (view == null) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.hot_search_item, viewGroup, false);
                viewHolder = new HotSearchItemViewHolder();
                viewHolder.rankTextView = view.findViewById(R.id.hot_search_rank_text_view);
                viewHolder.contentTextView = view.findViewById(R.id.hot_search_news_content_text_view);
                viewHolder.noteImageView = view.findViewById(R.id.hot_search_note_image_view);
                viewHolder.noteTextView = view.findViewById(R.id.hot_search_note_text_view);
                view.setTag(viewHolder);
            } else {
                viewHolder = (HotSearchItemViewHolder) view.getTag();
            }

            News currentNews = (News) getItem(i);

            viewHolder.rankTextView.setText(String.valueOf(i + 1));

            viewHolder.contentTextView.setText(currentNews.getContent());

            if (currentNews.getPaidByUser()) {
                viewHolder.noteImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_gold));
                viewHolder.noteTextView.setText(String.valueOf(currentNews.getPaidPrice()));
            } else {
                viewHolder.noteTextView.setText(String.valueOf(currentNews.getHotScore()));
                if (currentNews instanceof SuperNews) {
                    viewHolder.noteImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_rocket));
                } else if (currentNews instanceof NormalNews) {
                    viewHolder.noteImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_fire));
                }
            }

            LinearLayout.LayoutParams imageLayoutParams = new LinearLayout.LayoutParams(64, 64);
            viewHolder.noteImageView.setLayoutParams(imageLayoutParams);

            return view;
        }

        class HotSearchItemViewHolder {
            TextView rankTextView;
            TextView contentTextView;
            ImageView noteImageView;
            TextView noteTextView;
        }
    }
}