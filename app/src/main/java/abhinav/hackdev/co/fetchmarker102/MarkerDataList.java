package abhinav.hackdev.co.fetchmarker102;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MarkerDataList {

    @SerializedName("markers")
    private List<MarkerData> markerDataList ;

    public List<MarkerData> getMarkerDataList() {
        return markerDataList;
    }

    public void setMarkerDataList(List<MarkerData> markerDataList) {
        this.markerDataList = markerDataList;
    }

    public class MarkerData{

        @SerializedName("lat")
        private float latVal ;

        @SerializedName("long")
        private float longVal ;

        private String imgUrl ;
        private String vidUrl ;
        private String username ;

        public MarkerData() {
        }

        public float getLatVal() {
            return latVal;
        }

        public void setLatVal(float latVal) {
            this.latVal = latVal;
        }

        public float getLongVal() {
            return longVal;
        }

        public void setLongVal(float longVal) {
            this.longVal = longVal;
        }

        public String getImgUrl() {
            return imgUrl;
        }

        public void setImgUrl(String imgUrl) {
            this.imgUrl = imgUrl;
        }

        public String getVidUrl() {
            return vidUrl;
        }

        public void setVidUrl(String vidUrl) {
            this.vidUrl = vidUrl;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }

}
