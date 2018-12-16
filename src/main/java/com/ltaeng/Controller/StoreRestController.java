package com.ltaeng.Controller;

import com.ltaeng.Domain.*;
import com.ltaeng.Domain.rest.StoreDetail.StoreDetail;
import com.ltaeng.Domain.rest.Keyword;
import com.ltaeng.Domain.rest.KeywordStoreList.StoreList;
import com.ltaeng.Repository.StoreImageMapper;
import com.ltaeng.Repository.StoreMapper;
import com.ltaeng.Repository.StoreMenuMapper;
import com.ltaeng.Repository.StoreRateMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/store")
public class StoreRestController {
    //public static final String KAKAO_REST_ADDRESS_TO_MAP = "https://dapi.kakao.com/v2/local/search/address.json";
    public static final String KAKAO_REST_NEAR_STORE_LIST = "https://dapi.kakao.com/v2/local/search/category.json?category_group_code=FD6";
    public static final String KAKAO_REST_NEAR_STORE_LIST_KEYWORD = "https://dapi.kakao.com/v2/local/search/keyword.json";
    public static final String DAUM_MAP_DETAIL = "https://place.map.daum.net/main/v/";
    public static final String KAKAO_REST_API_KEY = "d90539dbd532694ddfe9f1c3e1166d05";

    public static final Random random = new Random();

    @Inject
    private StoreMapper storeMapper;
    @Inject
    private StoreRateMapper storeRateMapper;
    @Inject
    private StoreMenuMapper storeMenuMapper;
    @Inject
    private StoreImageMapper storeImageMapper;

    /*
    X, Y 좌표를 기준으로 Radius 안쪽의 음식점 리스트를 배포
     */
    @Transactional
    @RequestMapping(value="/list/map", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<com.ltaeng.Domain.rest.SimpleStoreList.StoreList> storeListByMap (
            @RequestParam(required = true) String x, @RequestParam(required = true) String y, @RequestParam(required = true) String radius,
            @RequestParam(required = false, defaultValue = "15") int totalNum, @RequestParam(required = false, defaultValue = "false") boolean recommend ) {
        com.ltaeng.Domain.rest.SimpleStoreList.StoreList storeList = new com.ltaeng.Domain.rest.SimpleStoreList.StoreList();
        storeList.setStoreList(getStoreList(x, y, radius, totalNum));
        if (recommend)
            storeList.setRecommend(getRecommendStore(storeList.getStoreList()));

        return new ResponseEntity<com.ltaeng.Domain.rest.SimpleStoreList.StoreList>(storeList, HttpStatus.OK);
    }

    /*
    사각형 좌표 안 쪽에 있는 음식점 리스트를 배포
     */
    @Transactional
    @RequestMapping(value="/list/map/rect", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<com.ltaeng.Domain.rest.SimpleStoreList.StoreList> storeListByMapRect (
            @RequestParam(required = true) String rect,
            @RequestParam(required = false, defaultValue = "15") int totalNum, @RequestParam(required = false, defaultValue = "false") boolean recommend ) {
        com.ltaeng.Domain.rest.SimpleStoreList.StoreList storeList = new com.ltaeng.Domain.rest.SimpleStoreList.StoreList();
        storeList.setStoreList(getStoreList(rect, totalNum));
        if (recommend)
            storeList.setRecommend(getRecommendStore(storeList.getStoreList()));

        return  new ResponseEntity<com.ltaeng.Domain.rest.SimpleStoreList.StoreList>(storeList, HttpStatus.OK);
    }

    /*
    키워드 주변의 음식점 리스트를 배포
     */
    @Transactional
    @RequestMapping(value = "/list/keyword/{keyword}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<StoreList> storeListByQuery(
            @PathVariable("keyword") String keyword, @RequestParam(required = true) String radius,
            @RequestParam(required = false, defaultValue = "3") int keywordNum, @RequestParam(required = false, defaultValue = "1") int keywordPage,
            @RequestParam(required = false, defaultValue = "15") int storeNum, @RequestParam(required = false, defaultValue = "false") boolean recommend) {
        StoreList storeList = new StoreList();
        storeList.setKeyword(new Keyword());
        storeList.setDocuments(new ArrayList<com.ltaeng.Domain.rest.KeywordStoreList.Document>());
        String x = null, y = null;
        int page = 1;

        if (keywordNum > 15) keywordNum = 15;
        if (storeNum > 15) storeNum = 15;
        for (page = 1; keywordPage * keywordNum > page * 15; page++);

        try {
            Document doc = Jsoup.connect(KAKAO_REST_NEAR_STORE_LIST_KEYWORD)
                    .header("Authorization", "KakaoAK " + KAKAO_REST_API_KEY)
                    .data("query", keyword)
                    .data("page", page + "")
                    .ignoreContentType(true)
                    .get();

            JSONObject keywordObject = new JSONObject(doc.text());
            for (int i = 0; i < ((keywordObject.getJSONArray("documents").length() < keywordNum) ? keywordObject.getJSONArray("documents").length() : keywordNum); i++) {
                int index = keywordNum * (keywordPage - 1) - 15 * (page - 1);
                if (index + i >= keywordObject.getJSONArray("documents").length()) index = 0;

                x = keywordObject.getJSONArray("documents").getJSONObject(index + i).getString("x");
                y = keywordObject.getJSONArray("documents").getJSONObject(index + i).getString("y");

                com.ltaeng.Domain.rest.KeywordStoreList.Document document = new com.ltaeng.Domain.rest.KeywordStoreList.Document();

                Store tmp = new Store();
                tmp.setDaumId(keywordObject.getJSONArray("documents").getJSONObject(index + i).getInt("id"));
                tmp.setName(keywordObject.getJSONArray("documents").getJSONObject(index + i).getString("place_name"));
                tmp.setAddress(keywordObject.getJSONArray("documents").getJSONObject(index + i).getString("address_name"));
                tmp.setX(keywordObject.getJSONArray("documents").getJSONObject(index + i).getString("x"));
                tmp.setY(keywordObject.getJSONArray("documents").getJSONObject(index + i).getString("y"));
                tmp.setPhone(keywordObject.getJSONArray("documents").getJSONObject(index + i).getString("phone"));
                tmp.setRate(-1);

                document.setPlace(tmp);
                if (x != null && y != null)
                    document.setNearStore(getStoreList(x, y, radius, storeNum));
                if (recommend)
                    document.setRecommend(getRecommendStore(document.getNearStore()));

                storeList.getDocuments().add(document);
            }

            storeList.getKeyword().setKeyword(keyword);
            storeList.getKeyword().setKeywordNum(storeList.getDocuments().size());
            storeList.getKeyword().setKeywordPage(keywordPage);
            storeList.getKeyword().setKeywordTotal(keywordObject.getJSONObject("meta").getInt("pageable_count"));

            return new ResponseEntity<StoreList>(storeList, HttpStatus.OK);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return new ResponseEntity<StoreList>(HttpStatus.NOT_FOUND);
    }

    /*
    키워드를 가지고 특정 음식점 상세 정보 배포
     */
    @Transactional
    @RequestMapping(value = "/detail/keyword/{keyword}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<StoreDetail> storeDetailByQuery(
            @PathVariable("keyword") String keyword,
            @RequestParam(required = false, defaultValue = "3") int keywordNum, @RequestParam(required = false, defaultValue = "1") int keywordPage,
            @RequestParam(required = false, defaultValue = "15") int storeNum) {
        StoreDetail storeDetail = new StoreDetail();
        storeDetail.setKeyword(new Keyword());
        storeDetail.setDetail(new ArrayList<StoreEnhanced>());
        String x = null, y = null;
        int page = 1;

        if (keywordNum > 15) keywordNum = 15;
        if (storeNum > 15) storeNum = 15;
        for (page = 1; keywordPage * keywordNum > page * 15; page++);

        try {
            Document doc = Jsoup.connect(KAKAO_REST_NEAR_STORE_LIST_KEYWORD)
                    .header("Authorization", "KakaoAK " + KAKAO_REST_API_KEY)
                    .data("category_group_code", "FD6")
                    .data("query", keyword)
                    .data("page", page + "")
                    .ignoreContentType(true)
                    .get();

            JSONObject keywordObject = new JSONObject(doc.text());
            for (int i = 0; i < ((keywordObject.getJSONArray("documents").length() < keywordNum) ? keywordObject.getJSONArray("documents").length() : keywordNum); i++) {
                int index = keywordNum * (keywordPage - 1) - 15 * (page - 1);
                if (index + i >= keywordObject.getJSONArray("documents").length()) index = 0;

                StoreNormal tmpStore = storeMapper.findStoreId(keywordObject.getJSONArray("documents").getJSONObject(index + i).getInt("id"));
                if (tmpStore == null) {
                    tmpStore = new StoreNormal();

                    tmpStore.setDaumId(keywordObject.getJSONArray("documents").getJSONObject(index + i).getInt("id"));
                    tmpStore.setName(keywordObject.getJSONArray("documents").getJSONObject(index + i).getString("place_name"));
                    tmpStore.setAddress(keywordObject.getJSONArray("documents").getJSONObject(index + i).getString("address_name"));
                    tmpStore.setX(keywordObject.getJSONArray("documents").getJSONObject(index + i).getString("x"));
                    tmpStore.setY(keywordObject.getJSONArray("documents").getJSONObject(index + i).getString("y"));
                    tmpStore.setPhone(keywordObject.getJSONArray("documents").getJSONObject(index + i).getString("phone"));
                    tmpStore.setRate(0);
                } else {
                    List<StoreRate> storeRateList = storeRateMapper.find(tmpStore.getId());
                    if (storeRateList.isEmpty())
                        tmpStore.setRate(0);
                    else {
                        int sum = 0;
                        for (int j = 0; j < storeRateList.size(); j++)
                            sum += storeRateList.get(j).getRate();
                        tmpStore.setRate(Math.round(sum * 100.0 / storeRateList.size()) / 100.0);
                    }
                }
                storeDetail.getDetail().add(getStoreDetail(tmpStore));
            }

            storeDetail.getKeyword().setKeyword(keyword);
            storeDetail.getKeyword().setKeywordNum(storeDetail.getDetail().size());
            storeDetail.getKeyword().setKeywordPage(keywordPage);
            storeDetail.getKeyword().setKeywordTotal(keywordObject.getJSONObject("meta").getInt("pageable_count"));

            return new ResponseEntity<StoreDetail>(storeDetail, HttpStatus.OK);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return new ResponseEntity<StoreDetail>(HttpStatus.NOT_FOUND);
    }

    /*
    음식점 ID를 가지고 특정 음식점 상세 정보 배포
     */
    @Transactional
    @RequestMapping(value = "/detail/{id}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<StoreEnhanced> storeDetailById(@PathVariable("id") int id) {
        StoreEnhanced storeEnhanced = null;
        StoreNormal tmpStore = storeMapper.find(id);
        if (tmpStore != null) {
            List<StoreRate> storeRateList = storeRateMapper.find(id);
            if (storeRateList.isEmpty())
                tmpStore.setRate(0);
            else {
                int sum = 0;
                for (int j = 0; j < storeRateList.size(); j++)
                    sum += storeRateList.get(j).getRate();
                tmpStore.setRate(Math.round(sum * 100.0 / storeRateList.size()) / 100.0);
            }

            storeEnhanced = getStoreDetail(tmpStore);
            return new ResponseEntity<StoreEnhanced>(storeEnhanced, HttpStatus.OK);
        }

        return new ResponseEntity<StoreEnhanced>(HttpStatus.NOT_FOUND);
    }



    @Transactional
    @RequestMapping(value="/rate/{id}", method= RequestMethod.GET, produces = "application/json")
    public ResponseEntity<StoreRate> temperature(@PathVariable("id") int id) {
        StoreRate storeRate = storeRateMapper.findById(id);
        if (storeRate == null)
            return new ResponseEntity<StoreRate>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<StoreRate>(storeRate, HttpStatus.OK);
    }

    @Transactional
    @RequestMapping(value = "/rate/upload/{id}", method = RequestMethod.POST)
    public ResponseEntity<Void> createTemperature(@RequestBody StoreRate storeRate, UriComponentsBuilder ucBuilder) {
        if (storeRateMapper.findById(storeRate.getId()) != null)
            return new ResponseEntity<Void>(HttpStatus.CONFLICT);

        storeRateMapper.insert(storeRate);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(
                ucBuilder.path("/rate/{id}").buildAndExpand(storeRate.getId()).toUri());
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }

    @Transactional
    @RequestMapping(value = "/rate/update/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Void> updateTemperature(@PathVariable("id") int id, @RequestBody StoreRate storeRate) {
        StoreRate storeRateById = storeRateMapper.findById(id);

        if (storeRateById == null)
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);

        storeRateById.setId(storeRate.getId());
        storeRateById.setRate(storeRate.getRate());
        storeRateById.setStoreId(storeRate.getStoreId());
        storeRateById.setUserId(storeRate.getUserId());

        storeRateMapper.update(storeRateById);

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @Transactional
    @RequestMapping(value = "/rate/delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<StoreRate> deleteTemperature(@PathVariable("id") int id) {
        StoreRate storeRate = storeRateMapper.findById(id);

        if (storeRate == null)
            return new ResponseEntity<StoreRate>(HttpStatus.NOT_FOUND);

        storeRateMapper.delete(storeRate.getId());
        return new ResponseEntity<StoreRate>(HttpStatus.NO_CONTENT);
    }


    public StoreEnhanced getRecommendStore(List<Store> storeList) {
        if (storeList.size() == 0) return null;
        int index = random.nextInt(storeList.size());
        StoreNormal tmpStore = new StoreNormal(storeList.get(index));
        tmpStore.setFlag("");
        tmpStore.setPicture("");

        return getStoreDetail(tmpStore);
    }

    public List<Store> getStoreList(String x, String y, String radius, int totalNum) {
        int page = 1;
        JSONObject metaInfo;
        ArrayList<Store> result = new ArrayList<>();

        try {
            do {
                Document doc = Jsoup.connect(KAKAO_REST_NEAR_STORE_LIST)
                        .header("Authorization", "KakaoAK " + KAKAO_REST_API_KEY)
                        .data("x", x)
                        .data("y", y)
                        .data("radius", radius)
                        .data("page", page++ + "")
                        .ignoreContentType(true)
                        .get();

                JSONObject storeListObject = new JSONObject(doc.text());
                metaInfo = storeListObject.getJSONObject("meta");

                JSONArray documentInfo = storeListObject.getJSONArray("documents");
                for (int i = 0; i < documentInfo.length(); i++) {
                    if (result.size() >= totalNum) break;
                    Store tmpStore = new Store();
                    StoreNormal tmpStoreNormal = storeMapper.findStoreId(documentInfo.getJSONObject(i).getInt("id"));

                    if (tmpStoreNormal == null) {
                        tmpStore.setDaumId(documentInfo.getJSONObject(i).getInt("id"));
                        tmpStore.setName(documentInfo.getJSONObject(i).getString("place_name"));
                        tmpStore.setAddress(documentInfo.getJSONObject(i).getString("address_name"));
                        tmpStore.setX(documentInfo.getJSONObject(i).getString("x"));
                        tmpStore.setY(documentInfo.getJSONObject(i).getString("y"));
                        tmpStore.setPhone(documentInfo.getJSONObject(i).getString("phone"));

                        StoreNormal storeNormal = new StoreNormal(tmpStore);
                        storeMapper.insert(storeNormal);
                        tmpStore.setId(storeNormal.getId());

                        //tmpStore = (StoreNormal)getStoreDetail(tmpStore);
                    } else {
                        tmpStore.setId(tmpStoreNormal.getId());
                        tmpStore.setDaumId(tmpStoreNormal.getDaumId());
                        tmpStore.setName(tmpStoreNormal.getName());
                        tmpStore.setAddress(tmpStoreNormal.getAddress());
                        tmpStore.setX(tmpStoreNormal.getX());
                        tmpStore.setY(tmpStoreNormal.getY());
                        tmpStore.setPhone(tmpStoreNormal.getPhone());

                        List<StoreRate> storeRateList = storeRateMapper.find(tmpStore.getId());
                        if (storeRateList.isEmpty())
                            tmpStore.setRate(0);
                        else {
                            int sum = 0;
                            for (int j = 0; j < storeRateList.size(); j++)
                                sum += storeRateList.get(j).getRate();
                            tmpStore.setRate(Math.round(sum * 100.0 / storeRateList.size()) / 100.0);
                        }
                    }

                    result.add(tmpStore);
                }

            } while (!metaInfo.getBoolean("is_end"));

        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public List<Store> getStoreList(String rect, int totalNum) {
        int page = 1;
        JSONObject metaInfo;
        ArrayList<Store> result = new ArrayList<>();

        try {
            do {
                Document doc = Jsoup.connect(KAKAO_REST_NEAR_STORE_LIST)
                        .header("Authorization", "KakaoAK " + KAKAO_REST_API_KEY)
                        .data("rect", rect)
                        .data("page", page++ + "")
                        .ignoreContentType(true)
                        .get();

                JSONObject storeListObject = new JSONObject(doc.text());
                metaInfo = storeListObject.getJSONObject("meta");

                JSONArray documentInfo = storeListObject.getJSONArray("documents");
                for (int i = 0; i < documentInfo.length(); i++) {
                    if (result.size() >= totalNum) break;
                    Store tmpStore = new Store();
                    StoreNormal tmpStoreNormal = storeMapper.findStoreId(documentInfo.getJSONObject(i).getInt("id"));

                    if (tmpStoreNormal == null) {
                        tmpStore.setDaumId(documentInfo.getJSONObject(i).getInt("id"));
                        tmpStore.setName(documentInfo.getJSONObject(i).getString("place_name"));
                        tmpStore.setAddress(documentInfo.getJSONObject(i).getString("address_name"));
                        tmpStore.setX(documentInfo.getJSONObject(i).getString("x"));
                        tmpStore.setY(documentInfo.getJSONObject(i).getString("y"));
                        tmpStore.setPhone(documentInfo.getJSONObject(i).getString("phone"));
                        tmpStore.setRate(0);

                        StoreNormal storeNormal = new StoreNormal(tmpStore);
                        storeMapper.insert(storeNormal);
                        tmpStore.setId(storeNormal.getId());

                    } else {
                        tmpStore.setId(tmpStoreNormal.getId());
                        tmpStore.setDaumId(tmpStoreNormal.getDaumId());
                        tmpStore.setName(tmpStoreNormal.getName());
                        tmpStore.setAddress(tmpStoreNormal.getAddress());
                        tmpStore.setX(tmpStoreNormal.getX());
                        tmpStore.setY(tmpStoreNormal.getY());
                        tmpStore.setPhone(tmpStoreNormal.getPhone());

                        List<StoreRate> storeRateList = storeRateMapper.find(tmpStore.getId());
                        if (storeRateList.isEmpty())
                            tmpStore.setRate(0);
                        else {
                            int sum = 0;
                            for (int j = 0; j < storeRateList.size(); j++)
                                sum += storeRateList.get(j).getRate();
                            tmpStore.setRate(Math.round(sum * 100.0 / storeRateList.size()) / 100.0);
                        }
                    }

                    result.add(tmpStore);
                }

            } while (!metaInfo.getBoolean("is_end"));

        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Transactional
    public StoreEnhanced getStoreDetail(StoreNormal tmpStore) {
        StoreEnhanced storeEnhanced = new StoreEnhanced();

        storeEnhanced.setId(tmpStore.getId());
        storeEnhanced.setDaumId(tmpStore.getDaumId());
        storeEnhanced.setName(tmpStore.getName());
        storeEnhanced.setAddress(tmpStore.getAddress());
        storeEnhanced.setX(tmpStore.getX());
        storeEnhanced.setY(tmpStore.getY());
        storeEnhanced.setPhone(tmpStore.getPhone());
        storeEnhanced.setRate(tmpStore.getRate());

        if (tmpStore.getId() == 0 || tmpStore.getFlag().equals("") || tmpStore.getPicture().equals("")) {
            try {
                Document detail = Jsoup.connect(DAUM_MAP_DETAIL + tmpStore.getDaumId())
                        .header("origin", "place.map.daum.net")
                        .header("referer", "https://place.map.daum.net/" + tmpStore.getDaumId())
                        .header("accept-encoding", "gzip, deflate, br")
                        .data("con", "1")
                        .data("rev", "4")
                        .data("r_enc", "UTF-8")
                        .data("st", "100")        // 각 파라미터가 무엇을 뜻하는지를 확인해 적절하게 사용하는 것도 좋지만
                        .data("q_enc", "UTF-8")   // 비정상적인 요청으로 감지해 아이디나 아이피가 밴 될 우려도 있으므로
                        .data("r_format", "json") // 특별한 이유가 없다면 모두 포함하는 것이 좋다.
                        .data("t_koreng", "1")
                        .data("ans", "2")
                        .data("run", "2")
                        .ignoreContentType(true) // HTML Document가 아니므로 Response의 컨텐트 타입을 무시한다.
                        .get();

                // org.json 라이브러리를 사용해 결과를 파싱한다.
                JSONObject detailObject = new JSONObject(detail.text());
                JSONObject basicInfo = detailObject.getJSONObject("basicInfo");
                //JSONObject rateInfo = basicInfo.getJSONObject("feedback");

                if (basicInfo.has("mainphotourl"))
                    tmpStore.setPicture(basicInfo.getString("mainphotourl"));
                else
                    tmpStore.setPicture("");

                String flag = "";
                if (basicInfo.has("operationInfo")) {
                    JSONObject operationInfo = basicInfo.getJSONObject("operationInfo");
                    if (operationInfo.has("appointment")) {
                        if (operationInfo.getString("appointment").equals("Y"))
                            flag += "예약/Y|";
                        else
                            flag += "예약/N|";
                    }
                    if (operationInfo.has("pagekage")) {
                        if (operationInfo.getString("pagekage").equals("Y"))
                            flag += "포장/Y|";
                        else
                            flag += "포장/N|";
                    }
                    if (operationInfo.has("delivery")) {
                        if (operationInfo.getString("delivery").equals("Y"))
                            flag += "배달/Y|";
                        else
                            flag += "배달/N|";
                    }
                }
                tmpStore.setFlag(flag);

                if (storeMapper.find(tmpStore.getId()) == null)
                    storeMapper.insert(tmpStore);
                else
                    storeMapper.update(tmpStore);

                storeEnhanced.setId(tmpStore.getId());
                storeEnhanced.setFlag(tmpStore.getFlag());
                storeEnhanced.setPicture(tmpStore.getPicture());
                storeEnhanced.setRate(tmpStore.getRate());

                List<StoreMenu> menuList = new ArrayList<StoreMenu>();
                if (detailObject.has("menuInfo")) {
                    JSONArray menuInfo = detailObject.getJSONObject("menuInfo").getJSONArray("menuList");

                    for (int j = 0; j < menuInfo.length(); j++) {
                        StoreMenu menu = new StoreMenu();
                        menu.setStoreId(tmpStore.getId());
                        menu.setName(menuInfo.getJSONObject(j).getString("menu"));
                        if (menuInfo.getJSONObject(j).has("price"))
                            menu.setPrice(menuInfo.getJSONObject(j).getString("price"));
                        else
                            menu.setPrice("");

                        storeMenuMapper.insert(menu);
                        menuList.add(menu);
                    }

                    storeEnhanced.setMenu(menuList);
                }

                List<StoreImage> imageList = new ArrayList<StoreImage>();
                if (detailObject.getJSONObject("photo").has("photoList")) {
                    JSONArray photoList = detailObject.getJSONObject("photo").getJSONArray("photoList").getJSONObject(0).getJSONArray("list");

                    for (int j = 0; j < photoList.length(); j++) {
                        StoreImage image = new StoreImage();
                        image.setStoreId(tmpStore.getId());
                        image.setPath(photoList.getJSONObject(j).getString("orgurl"));

                        storeImageMapper.insert(image);
                        imageList.add(image);
                    }

                    storeEnhanced.setImage(imageList);
                }
                //Thread.sleep(2000);
            }
            catch(IOException e) {
                e.printStackTrace();
            }
            /*
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            */


        } else {
            storeEnhanced.setFlag(tmpStore.getFlag());
            storeEnhanced.setPicture(tmpStore.getPicture());
            storeEnhanced.setRate(tmpStore.getRate());

            List<StoreMenu> storeMenuList = storeMenuMapper.find(tmpStore.getId());
            List<StoreImage> storeImageList = storeImageMapper.find(tmpStore.getId());

            storeEnhanced.setMenu(storeMenuList);
            storeEnhanced.setImage(storeImageList);

        }

        return storeEnhanced;
    }



}
