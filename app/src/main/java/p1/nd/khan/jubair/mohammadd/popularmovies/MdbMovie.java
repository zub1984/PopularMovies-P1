package p1.nd.khan.jubair.mohammadd.popularmovies;

/**
 * Created by Jubair on 10/19/2015.
 */

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Implementing Parcelable
 * http://www.developerphil.com/parcelable-vs-serializable/
 * http://stackoverflow.com/questions/12503836/how-to-save-custom-arraylist-on-android-screen-rotate
 */
/*************************************************************************************************************/
/*#################### Not in use, ToDo: will be used while implementing Retrofit ###########################*/
/*************************************************************************************************************/

public class MdbMovie implements Parcelable {

    private String mBackdropPath;
    private String mId;
    private String mTitle;
    private String mOverview;
    private String mPosterUrl;
    private String mReleaseDate;
    private String mUserRating;
    private String mVoteCount;

    //Take Json string and create an instance
    public MdbMovie(String mBackdropPath, String id, String title, String overview, String poster, String releaseDate, String userRating, String voteCount) {
        this.mBackdropPath = mBackdropPath;
        this.mId = id;
        this.mTitle = title;
        this.mOverview = overview;
        this.mPosterUrl = poster;
        this.mReleaseDate = releaseDate;
        this.mUserRating = userRating;
        this.mVoteCount = voteCount;
    }

    public String getPosterUrl() {
        return mPosterUrl;
    }

    public String getBackdropPath() {
        return mBackdropPath;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getOverview() {
        return mOverview;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public String getUserRating() {
        return mUserRating;
    }

    private MdbMovie(Parcel in) {
        mBackdropPath = in.readString();
        mId = in.readString();
        mTitle = in.readString();
        mOverview = in.readString();
        mPosterUrl = in.readString();
        mReleaseDate = in.readString();
        mUserRating = in.readString();
        mVoteCount = in.readString();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mBackdropPath);
        out.writeString(mId);
        out.writeString(mTitle);
        out.writeString(mOverview);
        out.writeString(mPosterUrl);
        out.writeString(mReleaseDate);
        out.writeString(mUserRating);
        out.writeString(mVoteCount);
    }

    public static final Creator<MdbMovie> CREATOR = new Creator<MdbMovie>() {
        public MdbMovie createFromParcel(Parcel in) {
            return new MdbMovie(in);
        }

        public MdbMovie[] newArray(int size) {
            return new MdbMovie[size];
        }
    };
}
