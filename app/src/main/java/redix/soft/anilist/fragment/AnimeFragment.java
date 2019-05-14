package redix.soft.anilist.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import at.blogc.android.views.ExpandableTextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import redix.soft.anilist.R;
import redix.soft.anilist.activity.MainActivity;
import redix.soft.anilist.adapter.AnimeAdapter;
import redix.soft.anilist.adapter.CharacterAdapter;
import redix.soft.anilist.adapter.GenreAdapter;
import redix.soft.anilist.adapter.NewsAdapter;
import redix.soft.anilist.api.JikanService;
import redix.soft.anilist.databinding.FragmentAnimeBinding;

import redix.soft.anilist.model.Anime;
import redix.soft.anilist.model.Character;
import redix.soft.anilist.model.Genre;
import redix.soft.anilist.model.News;
import redix.soft.anilist.model.Related;
import redix.soft.anilist.util.AnimationUtil;
import redix.soft.anilist.util.ChipsLayoutManagerHelper;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AnimeFragment extends Fragment {

    public static final String TAG = "Anime Details";

    @BindView(R.id.anime_progress) View progress;
    @BindView(R.id.characters_progress) View progressCharacters;
    @BindView(R.id.recommendations_progress) View progressRecom;

    @BindView(R.id.anime_main_layout) View mainLayout;
    @BindView(R.id.anime_expand_arrow) View expandArrow;
    @BindView(R.id.anime_synopsis) ExpandableTextView synopsis;

    @BindView(R.id.anime_genre_list) RecyclerView genreList;
    @BindView(R.id.anime_characters_list) RecyclerView characterList;
    @BindView(R.id.anime_adaptation_list) RecyclerView adaptationList;
    @BindView(R.id.anime_prequel_list) RecyclerView prequelList;
    @BindView(R.id.anime_sequel_list) RecyclerView sequelList;
    @BindView(R.id.anime_summary_list) RecyclerView summaryList;
    @BindView(R.id.anime_alternative_list) RecyclerView alternativeList;
    @BindView(R.id.anime_sidestory_list) RecyclerView sidestoryList;
    @BindView(R.id.anime_spinoff_list) RecyclerView spinoffList;
    @BindView(R.id.anime_character_list) RecyclerView relatedCharacterList;
    @BindView(R.id.anime_other_list) RecyclerView otherList;
    @BindView(R.id.anime_recommendations_list) RecyclerView recommendationsList;

    @BindView(R.id.anime_adaptation_layout) View adapatationLayout;
    @BindView(R.id.anime_alternative_layout) View alternativeLayout;
    @BindView(R.id.anime_prequel_layout) View prequelLayout;
    @BindView(R.id.anime_sequel_layout) View sequelLayout;
    @BindView(R.id.anime_summary_layout) View summaryLayout;
    @BindView(R.id.anime_sidestory_layout) View sidestoryLayout;
    @BindView(R.id.anime_spinoff_layout) View spinoffLayout;
    @BindView(R.id.anime_character_layout) View relatedCharacterLayout;
    @BindView(R.id.anime_other_layout) View otherLayout;

    private FragmentAnimeBinding animeBinding;
    private GenreAdapter genreAdapter;
    private CharacterAdapter characterAdapter;
    private AnimeAdapter recommendationsAdapter;

    private int animeId;

    public void setAnimeId(int animeId) {
        this.animeId = animeId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        animeBinding = DataBindingUtil.inflate( inflater,
                                                R.layout.fragment_anime,
                                                container,
                                                false);
        View view = animeBinding.getRoot();

        ButterKnife.bind(this, view);

        //Genres List
        genreAdapter = new GenreAdapter(new ArrayList<>(), getContext(), R.layout.list_genre);
        genreList.setLayoutManager(ChipsLayoutManagerHelper.build(getContext()));
        genreList.setAdapter(genreAdapter);

        //Characters List
        characterAdapter = new CharacterAdapter(new ArrayList<>(), getContext());
        characterList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        characterList.setAdapter(characterAdapter);

        //Recommendations List
        recommendationsAdapter = new AnimeAdapter(new ArrayList<>(), getContext(), R.layout.list_airing);
        recommendationsList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recommendationsList.setAdapter(recommendationsAdapter);

        getAnimeInfo(animeId);
        getAnimeCharacters(animeId);
        //new Handler().postDelayed(() -> getAnimeNews(animeId), 3000);
        new Handler().postDelayed(() -> getAnimeRecommendations(animeId), 3000);

        return view;
    }

    private void getAnimeInfo(int id){
        new JikanService()
                .getAnimeInfo(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        anime -> {
                            this.animeBinding.setAnime(anime);
                            List<Genre> genres = anime.getGenres();
                            if(genres.size() > 5)
                                genres = genres.subList(0, 5);
                            genreAdapter.setDataSet(genres);

                            setRelatedList();

                            AnimationUtil.collapse(progress);
                            AnimationUtil.fadeIn(mainLayout);
                            },
                        throwable -> Log.d("ERROR", throwable.getMessage())
                );
    }

    private void getAnimeCharacters(int id){
        new JikanService()
                .getAnimeCharacters(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        response -> {
                            progressCharacters.setVisibility(View.GONE);
                            List<Character> characters = response.getCharacters();
                            characterAdapter.setAllCharacters(characters);
                            if(characters.size() > 9)
                                characters = characters.subList(0, 9);
                            characterAdapter.setDataSet(characters);
                            },
                        throwable -> Log.d("ERROR", throwable.getMessage())
                );
    }

    /*private void getAnimeNews(int id){
        new JikanService()
                .getAnimeNews(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    progressNews.setVisibility(View.GONE);
                    List<News> news = response.getArticles();
                    newsAdapter.setDataSet(news);
                });
    }*/

    private void getAnimeRecommendations(int id){
        new JikanService()
                .getAnimeRecommendations(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        response -> {
                            List<Anime> animes = response.getAnimes();
                            if(animes.size() > 10)
                                animes = animes.subList(0, 10);
                            recommendationsAdapter.setDataSet(animes);
                            progressRecom.setVisibility(View.GONE);
                            },
                        throwable -> Log.d("ERROR", throwable.getMessage())
                );
    }

    private void setRelatedList() {
        Anime anime = this.animeBinding.getAnime();
        Related relatedAnime = anime.getRelated();

        //Adaptation
        if (relatedAnime.getAdaptation() != null)
            adaptationList.setAdapter(new AnimeAdapter(relatedAnime.getAdaptation(), getContext(), R.layout.list_related));
        else
            adapatationLayout.setVisibility(View.GONE);
        //Prequel
        if (relatedAnime.getPrequel() != null)
            prequelList.setAdapter(new AnimeAdapter(relatedAnime.getPrequel(), getContext(), R.layout.list_related));
        else
            prequelLayout.setVisibility(View.GONE);
        //Sequel
        if (relatedAnime.getSequel() != null)
            sequelList.setAdapter(new AnimeAdapter(relatedAnime.getSequel(), getContext(), R.layout.list_related));
        else
            sequelLayout.setVisibility(View.GONE);
        //Summary
        if (relatedAnime.getSummary() != null)
            summaryList.setAdapter(new AnimeAdapter(relatedAnime.getSummary(), getContext(), R.layout.list_related));
        else
            summaryLayout.setVisibility(View.GONE);
        //Alternative Version
        if (relatedAnime.getAlternativeVersion() != null)
            alternativeList.setAdapter(new AnimeAdapter(relatedAnime.getAlternativeVersion(), getContext(), R.layout.list_related));
        else
            alternativeLayout.setVisibility(View.GONE);
        //Side Story
        if (relatedAnime.getSideStory() != null)
            sidestoryList.setAdapter(new AnimeAdapter(relatedAnime.getSideStory(), getContext(), R.layout.list_related));
        else
            sidestoryLayout.setVisibility(View.GONE);
        //Spin-Off
        if (relatedAnime.getSpinOff() != null)
            spinoffList.setAdapter(new AnimeAdapter(relatedAnime.getSpinOff(), getContext(), R.layout.list_related));
        else
            spinoffLayout.setVisibility(View.GONE);
        //Character
        if (relatedAnime.getCharacter() != null)
            relatedCharacterList.setAdapter(new AnimeAdapter(relatedAnime.getCharacter(), getContext(), R.layout.list_related));
        else
            relatedCharacterLayout.setVisibility(View.GONE);
        //Character
        if (relatedAnime.getOther() != null)
            otherList.setAdapter(new AnimeAdapter(relatedAnime.getOther(), getContext(), R.layout.list_related));
        else
            otherLayout.setVisibility(View.GONE);
    }

    @OnClick(R.id.anime_synopsis_layout)
    public void onClickExpand(){
        if(synopsis.isExpanded())
            synopsis.collapse();
        else {
            synopsis.expand();
        }

        AnimationUtil.rotate(expandArrow, 180, 500);
    }

    @OnClick(R.id.anime_themes)
    public void onClickThemes(){
        loadListFragment(ListFragment.TYPES.THEMES);
    }

    @OnClick(R.id.anime_episodes)
    public void onClickEpisodes(){
        loadListFragment(ListFragment.TYPES.EPISODES);
    }

    @OnClick(R.id.anime_pictures)
    public void onClickPictures(){
        loadListFragment(ListFragment.TYPES.PICTURES);
    }

    private void loadListFragment(ListFragment.TYPES type){
        ListFragment fragment = new ListFragment();
        fragment.setType(type);
        fragment.setAnime(animeBinding.getAnime());

        ((MainActivity) getContext()).loadFragment(fragment, ListFragment.TAG);
    }

}
