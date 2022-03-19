package com.dharma.shopinar;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;

import com.bumptech.glide.Glide;
import com.dharma.shopinar.models.Item;
import com.dharma.shopinar.models.NotificationModel;
import com.dharma.shopinar.models.UserInfoModel;
import com.dharma.shopinar.networksync.CheckInternetConnection;
import com.dharma.shopinar.usersession.UserSession;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.glide.slider.library.animations.DescriptionAnimation;
import com.glide.slider.library.slidertypes.BaseSliderView;
import com.glide.slider.library.slidertypes.TextSliderView;
import com.glide.slider.library.tricks.ViewPagerEx;
import com.bumptech.glide.request.RequestOptions;
import com.glide.slider.library.SliderLayout;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mikepenz.crossfadedrawerlayout.view.CrossfadeDrawerLayout;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.MiniDrawer;
import com.mikepenz.materialdrawer.interfaces.ICrossfader;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerUIUtils;
import com.mikepenz.materialize.util.UIUtils;
import com.webianks.easy_feedback.EasyFeedback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.dharma.shopinar.LoginActivity.USERID;

public class MainActivity extends AppCompatActivity implements BaseSliderView.OnSliderClickListener,
        ViewPagerEx.OnPageChangeListener,View.OnClickListener {
    private static final String TAG = "MainActivity";

//    private SliderLayout sliderShow;
//    private Drawer result;
//    private CrossfadeDrawerLayout crossfadeDrawerLayout = null;

    private SliderLayout sliderShow;
    private Drawer result;
    private CrossfadeDrawerLayout crossfadeDrawerLayout = null;

    //to get user session data
    private UserSession session;
    private HashMap<String, String> user;
    private String name, email, profilePhoto, profile;
    private String first_time;
    private AccountHeader mHeaderResult;

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    FirebaseFirestore mStore;

    TextView category_title;
    View Chairs,Beds,Sofa,Couch,Desks,Tables;


    IntentFilter in = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
    CheckInternetConnection cic;

    public static int CHAIR = 1;
    public static int BED = 2;
    public static int SOFA = 3;
    public static int COUCH = 4;
    public static int DESK = 5;
    public static int TABLE = 6;
    private String mUserid = "default_user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Typeface typeface = ResourcesCompat.getFont(this, R.font.blacklist);
        TextView appname = findViewById(R.id.appname);
        appname.setTypeface(typeface);

        //check Internet Connection
//        new CheckInternetConnection(this).checkConnection();

        checkConnection();

        //create new session object by passing application context
        session = new UserSession(getApplicationContext());


        mUserid = getIntent().getStringExtra(USERID);

        mAuth = FirebaseAuth.getInstance();
//        mUser = FirebaseAuth.getInstance().getCurrentUser();
//        mUserid = FirebaseAuth.getInstance().getUid();



        mStore = FirebaseFirestore.getInstance();

        category_title = findViewById(R.id.category_title);

        //retrieve session values and display on listviews
//        getValues();

        initializeViews();


//        initializeProduct();

        //ImageSLider
        inflateImageSlider();


        if (session.getFirstTime()) {
            //tap target view
            tapview();
            session.setFirstTime(false);
        }
    }

    private void initializeViews() {
        Chairs = findViewById(R.id.chairs);
        Beds = findViewById(R.id.beds);
        Sofa = findViewById(R.id.sofa);
        Couch = findViewById(R.id.couch);
        Desks = findViewById(R.id.desks);
        Tables = findViewById(R.id.tables);

        Chairs.setOnClickListener(this);
        Beds.setOnClickListener(this);
        Sofa.setOnClickListener(this);
        Couch.setOnClickListener(this);
        Desks.setOnClickListener(this);
        Tables.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(MainActivity.this,CategoryActivity.class);

        switch (view.getId()) {
            case R.id.chairs:
                intent.putExtra("CATEGORY", "chairs");
                break;
            case R.id.beds:
                intent.putExtra("CATEGORY", "beds");
                break;
            case R.id.sofa:
                intent.putExtra("CATEGORY", "sofa");
                break;
            case R.id.couch:
                intent.putExtra("CATEGORY", "couch");
                break;
            case R.id.desks:
                intent.putExtra("CATEGORY", "desks");
                break;
            case R.id.tables:
                intent.putExtra("CATEGORY", "tables");
                break;
            default:
                break;
        }

        startActivity(intent);

    }

    private void initializeProduct() {


        ArrayList<String> listUrl = new ArrayList<>();
        ArrayList<String> listName = new ArrayList<>();

        List<NotificationModel>  notificationModels= new ArrayList<NotificationModel>();

        notificationModels.add(new NotificationModel("https://rb.gy/w3jsoi","New Designs","New Design are out \n\n" +
                "The furniture represents a combination of functionality, durability and stylish designs. Well finished pieces have no rough areas when you run your hands around the edges and sides."));

        notificationModels.add(new NotificationModel("https://rb.gy/puovbr","Grand Opening Sales",
                "The furniture represents a combination of functionality, durability and stylish designs. Well finished pieces have no rough areas when you run your hands around the edges and sides."));

        notificationModels.add(new NotificationModel("https://rb.gy/jolflh","Winter Sale Offers",
                "The furniture represents a combination of functionality, durability and stylish designs. Well finished pieces have no rough areas when you run your hands around the edges and sides."));

        notificationModels.add(new NotificationModel("https://rb.gy/gjl9qq","Organise",
                "The furniture represents a combination of functionality, durability and stylish designs. Well finished pieces have no rough areas when you run your hands around the edges and sides."));



        for(int i = 0 ; i < notificationModels.size() ; i++)
         mStore.collection("notification").add(notificationModels.get(i));

////        docRefUser.set(item);
//
//         CollectionReference docRefUser = mStore.collection("beds");
//         docRefUser.add(item);

//
//         String name;
//         int quantity;
//         int rating;
//         Double price;
//         Double cuttedPrice;
//         List<String> images = new ArrayList<String>();
//         boolean isAR;
//         String description;
//         String modelURL;
//
//
//         name = "Queen Size Bed 2 Blue";
////         name = "Study Table 1, The Home Dekor Solid Wood Office Table  (Free Standing, Finish Color - Natural Wood)";
//
//         price = 16999.0;
//         cuttedPrice = 18899.0;
////         price = 32595.0;
////         cuttedPrice = 48899.0;
////         images.add("");
//         images.add("https://firebasestorage.googleapis.com/v0/b/shopin-ar-75a3b.appspot.com/o/images%2Fcategory%2Fbeds%2Fqueen_size_bed_2_blue%2Fqueen_size_bed_2_blue_img1.jpg?alt=media&token=8a546ba1-bc6c-4ca1-a0f3-61069f786c3a");
//
//
//                 //         images.add("https://firebasestorage.googleapis.com/v0/b/shopin-ar-75a3b.appspot.com/o/images%2Fcategory%2Fdesks%2Fmodern_desk_1%2Fmodern_desk_1_img3.jpg?alt=media&token=a99d54c2-736c-44ab-a56c-320cd77125ad");
//
//        isAR=true;
//         quantity = 100;
//        rating = 10;
//       description = "Homes solid wood furniture is handcrafted by the finest artisans from Jodhpur. Woodgrain, color variance, texture changes and knots are all part of the charm of the product.The perfect furniture solution for compact urban homes with its simplistic design and built- in storage space. Make your sleeping time more comfortable by bringing home Bed with Storage. With great looks, this product also offers voluminous storage to keep your stuff inside. The attractive design on the headboard gives a modern appeal to your bedroom.High quality wood is used in crafting this Bed with Storage to make it robust and sturdy. The very beautiful Finish used in this bed gives a classic touch in the adornment.";
//       //                modelURL = "" ;
//                       modelURL = "https://firebasestorage.googleapis.com/v0/b/shopin-ar-75a3b.appspot.com/o/models%2Fcategory%2Fbeds%2Fqueen_size_bed_2_blue%2Fqueen_size_bed_2_blue.glb?alt=media&token=2ee16288-defc-4750-a54b-95b136d9ac1b" ;
//
//                         Item item = new Item(
//                name,
//                price,
//                cuttedPrice,
//                images,
//                isAR,
//                quantity,
//                rating,
//                description,
//                modelURL
//                );
//
////        mUserid = mAuth.getCurrentUser().getUid();
////        DocumentReference docRefUser = mStore.collection("chairs").document(item.getName().replaceAll(" ",""));
////        docRefUser.set(item);
//
//         CollectionReference docRefUser = mStore.collection("beds");
//         docRefUser.add(item);
    }


    private void getUserInformation() {

        DocumentReference docRef = mStore.collection("users").document(mUserid);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                UserInfoModel userInfo = documentSnapshot.toObject(UserInfoModel.class);

                String sessionName = userInfo.getName();
                String sessionPhone = userInfo.getPhone();
                String sessionEmail = userInfo.getEmail();
                String sessionProfile = userInfo.getProfile();


                name = userInfo.getName();
                email = userInfo.getEmail();
                profilePhoto = userInfo.getProfile();

                inflateNavDrawer();


                Log.e(TAG, sessionName
                        + " " + sessionPhone
                        + " " + sessionEmail
                        + " " + sessionProfile);
            }
        });

        // Count value of firebase cart and wishlist
        // countFirebaseValues();
    }



    private void inflateNavDrawer() {

        //set Custom toolbar to activity -----------------------------------------------------------
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the AccountHeader ----------------------------------------------------------------

        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                ;
                Glide.with(imageView.getContext()).load(uri).placeholder(placeholder).into(imageView);
//                Picasso.get().load(uri).into(imageView);
            }

            @Override
            public void cancel(ImageView imageView) {
//                imageView.setImageDrawable(R.drawable.default_profile);
            }
        });

        //Profile Making
        IProfile profile = new ProfileDrawerItem()
                .withName(name)
                .withEmail(email)
                .withIcon(profilePhoto);

        mHeaderResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.color.colorPrimary)
                .addProfiles(profile)
                .withCompactStyle(true)
                .build();



        //Adding nav drawer items ------------------------------------------------------------------
        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName(R.string.home).withIcon(R.drawable.home);
        PrimaryDrawerItem item2 = new PrimaryDrawerItem().withIdentifier(2).withName(R.string.myprofile).withIcon(R.drawable.profile);
        PrimaryDrawerItem item3 = new PrimaryDrawerItem().withIdentifier(3).withName(R.string.wishlist).withIcon(R.drawable.wishlist);
        PrimaryDrawerItem item4 = new PrimaryDrawerItem().withIdentifier(4).withName(R.string.cart).withIcon(R.drawable.cart);
        PrimaryDrawerItem item5 = new PrimaryDrawerItem().withIdentifier(5).withName(R.string.logout).withIcon(R.drawable.logout);

        SecondaryDrawerItem item7 = new SecondaryDrawerItem().withIdentifier(7).withName("Offers").withIcon(R.drawable.tag);
        SecondaryDrawerItem item8 = new SecondaryDrawerItem().withIdentifier(8).withName(R.string.aboutapp).withIcon(R.drawable.credits);
        SecondaryDrawerItem item9 = new SecondaryDrawerItem().withIdentifier(9).withName(R.string.feedback).withIcon(R.drawable.feedback);
        SecondaryDrawerItem item10 = new SecondaryDrawerItem().withIdentifier(10).withName(R.string.helpcentre).withIcon(R.drawable.helpccenter);

        SecondaryDrawerItem item12 = new SecondaryDrawerItem().withIdentifier(12).withName("App Tour").withIcon(R.drawable.tour);
        SecondaryDrawerItem item13 = new SecondaryDrawerItem().withIdentifier(13).withName("Explore").withIcon(R.drawable.explore);


        //creating navbar and adding to the toolbar ------------------------------------------------
        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withHasStableIds(true)
                .withDrawerLayout(R.layout.crossfade_drawer)
                .withAccountHeader(mHeaderResult)
                .withDrawerWidthDp(72)
                .withGenerateMiniDrawer(false)
                .withTranslucentStatusBar(true)
                .withActionBarDrawerToggleAnimated(true)
                .addDrawerItems(
                        item1, item2, item3, item4, item5, new DividerDrawerItem(), item7, item8, item9, item10, new DividerDrawerItem(), item12, item13
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                        switch (position) {

                            case 1:
                                if (result != null && result.isDrawerOpen()) {
                                    result.closeDrawer();
                                }
                                break;
                            case 2:
                                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                                break;
                            case 3:
                                startActivity(new Intent(MainActivity.this, Wishlist.class));
                                break;
                            case 4:
                                startActivity(new Intent(MainActivity.this, Cart.class));
                                break;
                            case 5:
                                session.logoutUser();
                                finish();
                                break;

                            case 7:
                                startActivity(new Intent(MainActivity.this, NotificationActivity.class));
                                break;

                            case 8 :
                                startActivity(new Intent(MainActivity.this, About.class));
                                break;

//                            case 8:
//                                new LibsBuilder()
//                                        .withFields(R.string.class.getFields())
//                                        .withActivityTitle(getString(R.string.about_activity_title))
//                                        .withAboutIconShown(true)
//                                        .withAboutAppName(getString(R.string.app_name))
//                                        .withAboutVersionShown(true)
//                                        .withLicenseShown(true)
//                                        .withAboutSpecial1(getString(R.string.domain))
//                                        .withAboutSpecial1Description(getString(R.string.website))
//                                        .withAboutSpecial2(getString(R.string.licence))
//                                        .withAboutSpecial2Description(getString(R.string.licencedesc))
//                                        .withAboutSpecial3(getString(R.string.changelog))
//                                        .withAboutSpecial3Description(getString(R.string.changes))
//                                        .withShowLoadingProgress(true)
//                                        .withAboutDescription(getString(R.string.about_activity_description))
//                                        .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
//                                        .start(MainActivity.this);
//                                break;
                            case 9:
                                Toast.makeText(MainActivity.this, "Coming Soon", Toast.LENGTH_LONG).show();

//                                new EasyFeedback.Builder(MainActivity.this)
//                                        .withEmail("dharmaraongu110798@gmail.com")
//                                        .withSystemInfo()
//                                        .build()
//                                        .start();
                                break;
                            case 10:
                                Toast.makeText(MainActivity.this, "Coming Soon", Toast.LENGTH_LONG).show();

//                                startActivity(new Intent(MainActivity.this, HelpCenter.class));
                                break;
                            case 12:
                                session.setFirstTimeLaunch(true);
                                startActivity(new Intent(MainActivity.this, WelcomeActivity.class));
                                finish();
                                break;
                            case 13:
                                if (result != null && result.isDrawerOpen()) {
                                    result.closeDrawer();
                                }
                                tapview();
                                break;
                            default:
                                Toast.makeText(MainActivity.this, "Default", Toast.LENGTH_LONG).show();

                        }

                        return true;
                    }
                })
                .build();


        //Setting crossfader drawer------------------------------------------------------------

        crossfadeDrawerLayout = (CrossfadeDrawerLayout) result.getDrawerLayout();
        //define maxDrawerWidth
        crossfadeDrawerLayout.setMaxWidthPx(DrawerUIUtils.getOptimalDrawerWidth(this));
        //add second view (which is the miniDrawer)
        final MiniDrawer miniResult = result.getMiniDrawer();

        //build the view for the MiniDrawer
        View view = miniResult.build(this);

        //set the background of the MiniDrawer as this would be transparent
        view.setBackgroundColor(UIUtils.getThemeColorFromAttrOrRes(this, com.mikepenz.materialdrawer.R.attr.material_drawer_background, com.mikepenz.materialdrawer.R.color.material_drawer_background));

        //we do not have the MiniDrawer view during CrossfadeDrawerLayout creation so we will add it here
        crossfadeDrawerLayout.getSmallView().addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        //define the crossfader to be used with the miniDrawer. This is required to be able to automatically toggle open / close
        miniResult.withCrossFader(new ICrossfader() {
            @Override
            public void crossfade() {
                boolean isFaded = isCrossfaded();
                crossfadeDrawerLayout.crossfade(0);

                //only close the drawer if we were already faded and want to close it now
                if (isFaded) {
                    result.getDrawerLayout().closeDrawer(GravityCompat.START);
                }
            }

            @Override
            public boolean isCrossfaded() {
                return crossfadeDrawerLayout.isCrossfaded();
            }
        });
    }


    private void inflateImageSlider() {

        sliderShow = findViewById(R.id.slider);

        ArrayList<String> listUrl = new ArrayList<>();
        ArrayList<String> listName = new ArrayList<>();

        listUrl.add("https://rb.gy/w3jsoi");
        listName.add("New Designs");

        listUrl.add("https://rb.gy/puovbr");
        listName.add("Grand Opening Sale");

        listUrl.add("https://rb.gy/jolflh");
        listName.add("Winter Sale Offers");

        listUrl.add("https://rb.gy/gjl9qq");
        listName.add("Organise");


        RequestOptions requestOptions = new RequestOptions();
        requestOptions.centerCrop();
        //.diskCacheStrategy(DiskCacheStrategy.NONE)
        //.placeholder(R.drawable.placeholder)
        //.error(R.drawable.placeholder);

        for (int i = 0; i < listUrl.size(); i++) {
            TextSliderView sliderView = new TextSliderView(this);
            // if you want show image only / without description text use DefaultSliderView instead

            // initialize SliderLayout
            sliderView
                    .image(listUrl.get(i))
                    .description(listName.get(i))
                    .setRequestOption(requestOptions)
                    .setProgressBarVisible(true)
                    .setOnSliderClickListener(this);

            //add your extra information
            sliderView.bundle(new Bundle());
            sliderView.getBundle().putString("extra", listName.get(i));
            sliderShow.addSlider(sliderView);
        }

        // set Slider Transition Animation
//         mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Default);
        sliderShow.setPresetTransformer(SliderLayout.Transformer.ZoomOutSlide);

        sliderShow.setPresetIndicator(SliderLayout.PresetIndicators.Right_Bottom);
        sliderShow.setCustomAnimation(new DescriptionAnimation());
        sliderShow.setDuration(4000);
        sliderShow.addOnPageChangeListener(this);
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    private void tapview() {

        new TapTargetSequence(this)
                .targets(
                        TapTarget.forView(findViewById(R.id.notifintro), "Notifications", "Latest offers will be available here !")
                                .targetCircleColor(R.color.whiteTextColor)
                                .titleTextColor(R.color.whiteTextColor)
                                .titleTextSize(25)
                                .descriptionTextSize(15)
                                .descriptionTextColor(R.color.whiteTextColor)
                                .drawShadow(true)                   // Whether to draw a drop shadow or not
                                .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                                .tintTarget(true)
                                .transparentTarget(true)
                                .outerCircleColor(R.color.first),
                        TapTarget.forView(findViewById(R.id.view_profile), "Profile", "You can view and edit your profile here !")
                                .targetCircleColor(R.color.whiteTextColor)
                                .titleTextColor(R.color.whiteTextColor)
                                .titleTextSize(25)
                                .descriptionTextSize(15)
                                .descriptionTextColor(R.color.whiteTextColor)
                                .drawShadow(true)                   // Whether to draw a drop shadow or not
                                .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                                .tintTarget(true)
                                .transparentTarget(true)
                                .outerCircleColor(R.color.third),
                        TapTarget.forView(findViewById(R.id.cart), "Your Cart", "Here is Shortcut to your cart !")
                                .targetCircleColor(R.color.whiteTextColor)
                                .titleTextColor(R.color.whiteTextColor)
                                .titleTextSize(25)
                                .descriptionTextSize(15)
                                .descriptionTextColor(R.color.whiteTextColor)
                                .drawShadow(true)
                                .cancelable(false)// Whether tapping outside the outer circle dismisses the view
                                .tintTarget(true)
                                .transparentTarget(true)
                                .outerCircleColor(R.color.second),
                        TapTarget.forView(findViewById(R.id.chairs), "Categories", "Product Categories have been listed here !")
                                .targetCircleColor(R.color.whiteTextColor)
                                .titleTextColor(R.color.whiteTextColor)
                                .titleTextSize(25)
                                .descriptionTextSize(15)
                                .descriptionTextColor(R.color.whiteTextColor)
                                .drawShadow(true)
                                .cancelable(false)// Whether tapping outside the outer circle dismisses the view
                                .tintTarget(true)
                                .transparentTarget(true)
                                .outerCircleColor(R.color.fourth))
                .listener(new TapTargetSequence.Listener() {
                    // This listener will tell us when interesting(tm) events happen in regards
                    // to the sequence
                    @Override
                    public void onSequenceFinish() {
                        session.setFirstTime(false);
                        Toast.makeText(MainActivity.this, " You are ready to go !", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {

                    }

                    @Override
                    public void onSequenceCanceled(TapTarget lastTarget) {
                        // Boo
                    }
                }).start();

    }


    public void viewProfile(View view) {
        startActivity(new Intent(MainActivity.this, ProfileActivity.class));
    }

    public void viewCart(View view) {
        startActivity(new Intent(MainActivity.this, Cart.class));
    }


    public void Notifications(View view) {
        startActivity(new Intent(MainActivity.this, NotificationActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
//        category_title.setPaintFlags(category_title.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        //check Internet Connection
        checkConnection();
        getUserInformation();


//        getUpdateOfDrawer();
    }

    private void getUpdateOfDrawer() {
        IProfile iProfile = new ProfileDrawerItem()
                .withName(name)
                .withEmail(email)
                .withIcon(profilePhoto);

        mHeaderResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.color.colorPrimary)
                .addProfiles(iProfile)
                .withCompactStyle(true)
                .build();

//        result.switchDrawerContent(mHeaderResult);
    }

    @Override
    protected void onStart() {
        super.onStart();
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if (currentUser != null){
//            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
//                mUserid = FirebaseAuth.getInstance().getCurrentUser().getUid();
//                getUserInformation();
//
//            } else {
//                SendUserToLoginActivity();
//            }
//        }else {
//            SendUserToLoginActivity();
//
//        }

    }

    private void SendUserToLoginActivity() {

        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }

    protected void onDestroy() {
        unregisterReceiver(cic);
        super.onDestroy();
    }


    void checkConnection() {
        IntentFilter in = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        CheckInternetConnection cic;
        cic = new CheckInternetConnection();
        this.cic = cic;
        registerReceiver(cic, in);
    }


    @Override
    public void onBackPressed() {
        moveTaskToBack(true); //it goes to background.
    }

    void onClick() {
        Intent in = new Intent(this, CategoryActivity.class);
        startActivity(in);
        finish();
    }
}