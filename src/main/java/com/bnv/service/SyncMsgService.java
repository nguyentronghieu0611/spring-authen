package com.bnv.service;

import com.bnv.model.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.StoredProcedureQuery;

@Service
public class SyncMsgService {
    private static String _header = "pkg_parse_json.parse_header_json";
    private static String _thongtinchung = "pkg_parse_json.parse_ns_thongtinchung_json";
    private static String _tuyendungquatrinhcongtac = "pkg_parse_json.parse_ns_tuyendung_qtct_json";
    private static String _quatrinhcongtacs = "pkg_parse_json.parse_ns_quatrinhcongtac_json";
    private static String _luongphucap = "pkg_parse_json.parse_ns_luongphucap_json";
    private static String _luongs = "pkg_parse_json.parse_ns_luong_json";
    private static String _phucaps = "pkg_parse_json.parse_ns_phucap_json";
    private static String _trinhdodaotaoboiduong = "pkg_parse_json.parse_ns_daotaoboiduong_json";
    private static String _daotaoboiduongs = "pkg_parse_json.parse_daotaoboiduong_json";
    private static String _tinhocs = "pkg_parse_json.parse_tinhoc_json";
    private static String _ngoaingus = "pkg_parse_json.parse_ngoaingu_json";
    private static String _thongtinkhac = "pkg_parse_json.parse_ns_thongtinkhac_json";
    private static String _danhgiaphanloais = "pkg_parse_json.parse_danhgiaphanloai_json";

    private static String _xoahosonhansu = "pkg_parse_json.parse_ns_xoahosonhansu_json";
    private static String _sohieucbccvc_bndp = "PKG_SELECT_JSON.SELECT_NS_HOSONHANSU_JSON";

    @Autowired
    EntityMsgService syncMsgService;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    public Response mumtilCallStoreProcedure(String madonvi, String json_header, String json_thongtinchung, String json_tuyendungquatrinhcongtac, String json_quatrinhcongtac,
                                             String json_luongphucapchucvu, String json_quatrinhphucap, String json_quatrinhluong, String json_trinhdodaotaoboiduong,
                                             String json_tinhoc, String json_ngoaingu, String json_quatrinhdaotaoboiduong, String json_thongtinkhac, String json_ketquadanhgia) {
        Response response = null;
        EntityManager em = null;
        EntityTransaction tx = null;
        try {

            em = entityManagerFactory.createEntityManager();
            tx = em.getTransaction();
            tx.begin();

            if (!json_header.isEmpty()) {
                StoredProcedureQuery header = syncMsgService.storeProcedures(em, _header, madonvi, json_header);
                response = getstoreProcedure((String) header.getOutputParameterValue("u_ret"));
                if (response.getErr_code() == 1) {
                    tx.rollback();
                    return response;
                }
            }
            if (!json_thongtinchung.isEmpty()) {
                StoredProcedureQuery thongtinchung = syncMsgService.storeProcedures(em, _thongtinchung, madonvi, json_thongtinchung);
                response = getstoreProcedure((String) thongtinchung.getOutputParameterValue("u_ret"));
                if (response.getErr_code() == 1) {
                    tx.rollback();
                    return response;
                }
            }
            if (!json_tuyendungquatrinhcongtac.isEmpty()) {
                StoredProcedureQuery tuyendungquatrinhcongtac = syncMsgService.storeProcedures(em, _tuyendungquatrinhcongtac, madonvi, json_tuyendungquatrinhcongtac);
                response = getstoreProcedure((String) tuyendungquatrinhcongtac.getOutputParameterValue("u_ret"));
                if (response.getErr_code() == 1) {
                    tx.rollback();
                    return response;
                }
            }

            if (!json_quatrinhcongtac.isEmpty()) {
                JSONArray quatrinhcongtacs = new JSONArray(json_quatrinhcongtac);
                for (int i = 0; i < quatrinhcongtacs.length(); i++) {
                    StoredProcedureQuery quatrinhcongtacQuery = syncMsgService.storeProcedures(em, _quatrinhcongtacs, madonvi, quatrinhcongtacs.getJSONObject(i).toString());
                    response = getstoreProcedure((String) quatrinhcongtacQuery.getOutputParameterValue("u_ret"));
                    if (response.getErr_code() == 1) {
                        tx.rollback();
                        return response;
                    }
                }
            }

            if (!json_luongphucapchucvu.isEmpty()) {
                StoredProcedureQuery luongphucapchucvu = syncMsgService.storeProcedures(em, _luongphucap, madonvi, json_luongphucapchucvu);
                response = getstoreProcedure((String) luongphucapchucvu.getOutputParameterValue("u_ret"));
                if (response.getErr_code() == 1) {
                    tx.rollback();
                    return response;
                }
            }

            if (!json_quatrinhphucap.isEmpty()) {
                JSONArray quatrinhphucaps = new JSONArray(json_quatrinhphucap);
                for (int i = 0; i < quatrinhphucaps.length(); i++) {
                    StoredProcedureQuery quatrinhphucapQuery = syncMsgService.storeProcedures(em, _phucaps, madonvi, quatrinhphucaps.getJSONObject(i).toString());
                    response = getstoreProcedure((String) quatrinhphucapQuery.getOutputParameterValue("u_ret"));
                    if (response.getErr_code() == 1) {
                        tx.rollback();
                        return response;
                    }
                }
            }

            if (!json_quatrinhluong.isEmpty()) {
                JSONArray quatrinhluongs = new JSONArray(json_quatrinhluong);
                for (int i = 0; i < quatrinhluongs.length(); i++) {
                    StoredProcedureQuery quatrinhluongQuery = syncMsgService.storeProcedures(em, _luongs, madonvi, quatrinhluongs.getJSONObject(i).toString());
                    response = getstoreProcedure((String) quatrinhluongQuery.getOutputParameterValue("u_ret"));
                    if (response.getErr_code() == 1) {
                        tx.rollback();
                        return response;
                    }
                }
            }

            if (!json_trinhdodaotaoboiduong.isEmpty()) {
                StoredProcedureQuery trinhdodaotaoboiduong = syncMsgService.storeProcedures(em, _trinhdodaotaoboiduong, madonvi, json_trinhdodaotaoboiduong);
                response = getstoreProcedure((String) trinhdodaotaoboiduong.getOutputParameterValue("u_ret"));
                if (response.getErr_code() == 1) {
                    tx.rollback();
                    return response;
                }
            }

            if (!json_tinhoc.isEmpty()) {
                JSONArray tinhocs = new JSONArray(json_tinhoc);
                for (int i = 0; i < tinhocs.length(); i++) {
                    StoredProcedureQuery ngoainguQuery = syncMsgService.storeProcedures(em, _tinhocs, madonvi, tinhocs.getJSONObject(i).toString());
                    response = getstoreProcedure((String) ngoainguQuery.getOutputParameterValue("u_ret"));
                    if (response.getErr_code() == 1) {
                        tx.rollback();
                        return response;
                    }
                }
            }

            if (!json_ngoaingu.isEmpty()) {
                JSONArray ngoaingus = new JSONArray(json_ngoaingu);
                for (int i = 0; i < ngoaingus.length(); i++) {
                    StoredProcedureQuery ngoainguQuery = syncMsgService.storeProcedures(em, _ngoaingus, madonvi, ngoaingus.getJSONObject(i).toString());
                    response = getstoreProcedure((String) ngoainguQuery.getOutputParameterValue("u_ret"));
                    if (response.getErr_code() == 1) {
                        tx.rollback();
                        return response;
                    }
                }
            }

            if (!json_quatrinhdaotaoboiduong.isEmpty()) {
                JSONArray quatrinhdaotaoboiduongs = new JSONArray(json_quatrinhdaotaoboiduong);
                for (int i = 0; i < quatrinhdaotaoboiduongs.length(); i++) {
                    StoredProcedureQuery quatrinhdaotaoboiduongQuery = syncMsgService.storeProcedures(em, _daotaoboiduongs, madonvi, quatrinhdaotaoboiduongs.getJSONObject(i).toString());
                    response = getstoreProcedure((String) quatrinhdaotaoboiduongQuery.getOutputParameterValue("u_ret"));
                    if (response.getErr_code() == 1) {
                        tx.rollback();
                        return response;
                    }
                }
            }

            if (!json_thongtinkhac.isEmpty()) {
                StoredProcedureQuery thongtinkhacQuery = syncMsgService.storeProcedures(em, _thongtinkhac, madonvi, json_thongtinkhac);
                response = getstoreProcedure((String) thongtinkhacQuery.getOutputParameterValue("u_ret"));
                if (response.getErr_code() == 1) {
                    tx.rollback();
                    return response;
                }
            }

            if (!json_ketquadanhgia.isEmpty()) {
                JSONArray ketquadanhgiaphanloais = new JSONArray(json_ketquadanhgia);
                for (int i = 0; i < ketquadanhgiaphanloais.length(); i++) {
                    StoredProcedureQuery ketquadanhgiaphanloaiQuery = syncMsgService.storeProcedures(em, _danhgiaphanloais, madonvi, ketquadanhgiaphanloais.getJSONObject(i).toString());
                    response = getstoreProcedure((String) ketquadanhgiaphanloaiQuery.getOutputParameterValue("u_ret"));
                    if (response.getErr_code() == 1) {
                        tx.rollback();
                        return response;
                    }
                }
            }

            tx.commit();
            em.close();
            response = new Response("Cập nhật dữ liệu cán bộ, công chức, viên chức thành công", 0);
        } catch (RuntimeException ex) {
            response = new Response("Cập nhật dữ liệu cán bộ, công chức, viên chức không thành công: Stored procedure query  " + ex.getMessage(), 1);
        } finally {
            if (tx != null && tx.isActive()) tx.rollback();
            if (em != null && em.isOpen()) em.close();
        }
        return response;
    }

    //    //thông tin chung
//    public Response setThongtinchung(JSONObject thongtinchung, String madonvi, String sohieucbccvc_bndp) {
//        Response response = null;
//        try {
//            thongtinchung.put("SoHieuCBCCVC_BNDP", sohieucbccvc_bndp);
//            response = getstoreProcedure(_ns_thongtinchung, madonvi, thongtinchung.toString());
//        } catch (Exception ex) {
//            response = new Response("Thông tin chung " + ex.getMessage(), 1);
//        }
//        return response;
//    }
//
//    //tuyển dụng, quá trình công tác
//    public Response setTuyendungquatrinhcongtac(JSONObject tuyendungquatrinhcongtac, String madonvi, String nhansu_id) {
//        Response response = null;
//        try {
//            tuyendungquatrinhcongtac.put("NhanSu_Id", nhansu_id);
//            response = getstoreProcedure(_ns_tuyendungquatrinhcongtac, madonvi, tuyendungquatrinhcongtac.toString());
//        } catch (Exception ex) {
//            response = new Response("Quá trình đào tạo bồi dưỡng " + ex.getMessage(), 1);
//        }
//        return response;
//    }
//
//    //quá trình công tác
//    public Response setQuatrinhcongtacs(JSONArray quatrinhcongtacs, String madonvi, String nhansu_id) {
//        Response response = null;
//        try {
//            for (int i = 0; i < quatrinhcongtacs.length(); i++) {
//                JSONObject quatrinhcongtac = quatrinhcongtacs.getJSONObject(i);
//                quatrinhcongtac.put("NhanSu_Id", nhansu_id);
//                response = getstoreProcedure(_ns_quatrinhcongtac, madonvi, quatrinhcongtac.toString());
//                if (response.getErr_code() == 1)
//                    break;
//            }
//        } catch (Exception ex) {
//            response = new Response("Danh sách lương " + ex.getMessage(), 1);
//        }
//        return response;
//    }
//
//    //luong phụ cấp chức vụ
//    public Response setLuongphucapchucvu(JSONObject luongphucapchucvu, String madonvi, String nhansu_id) {
//        Response response = null;
//        try {
//            luongphucapchucvu.put("NhanSu_Id", nhansu_id);
//            response = getstoreProcedure(_ns_luongphucap, madonvi, luongphucapchucvu.toString());
//        } catch (Exception ex) {
//            response = new Response("Quá trình đào tạo bồi dưỡng " + ex.getMessage(), 1);
//        }
//        return response;
//    }
//
//    //quá trình phụ cấp
//    public Response setQuatrinhphucap(JSONArray quatrinhphucaps, String madonvi, String nhansu_id) {
//        Response response = null;
//        try {
//            for (int i = 0; i < quatrinhphucaps.length(); i++) {
//                JSONObject quatrinhphucap = quatrinhphucaps.getJSONObject(i);
//                quatrinhphucap.put("NhanSu_Id", nhansu_id);
//                response = getstoreProcedure(_phucap, madonvi, quatrinhphucap.toString());
//                if (response.getErr_code() == 1)
//                    break;
//            }
//        } catch (Exception ex) {
//            response = new Response("Danh sách lương " + ex.getMessage(), 1);
//        }
//        return response;
//    }
//
//    //qua trình lương
//    public Response setQuatrinhluong(JSONArray quatrinhluongs, String madonvi, String nhansu_id) {
//        Response response = null;
//        try {
//            for (int i = 0; i < quatrinhluongs.length(); i++) {
//                JSONObject quatrinhluong = quatrinhluongs.getJSONObject(i);
//                quatrinhluong.put("NhanSu_Id", nhansu_id);
//                response = getstoreProcedure(_luong, madonvi, quatrinhluong.toString());
//                if (response.getErr_code() == 1)
//                    break;
//            }
//        } catch (Exception ex) {
//            response = new Response("Danh sách lương " + ex.getMessage(), 1);
//        }
//        return response;
//    }
//
//    //đào tạo bồi dưỡng
//    public Response setDaotaoboiduong(JSONObject daotaoboiduong, String madonvi, String nhansu_id) {
//        Response response = null;
//        try {
//            daotaoboiduong.put("NhanSu_Id", nhansu_id);
//            response = getstoreProcedure(_ns_daotaoboiduong, madonvi, daotaoboiduong.toString());
//        } catch (Exception ex) {
//            response = new Response("Quá trình đào tạo bồi dưỡng " + ex.getMessage(), 1);
//        }
//        return response;
//    }
//
//    //danh sách tin học
//    public Response setTinhocs(JSONArray tinhocs, String madonvi, String nhansu_id) {
//        Response response = null;
//        try {
//            for (int i = 0; i < tinhocs.length(); i++) {
//                JSONObject tinhoc = tinhocs.getJSONObject(i);
//                tinhoc.put("NhanSu_Id", nhansu_id);
//                response = getstoreProcedure(_tinhoc, madonvi, tinhoc.toString());
//                if (response.getErr_code() == 1)
//                    break;
//            }
//        } catch (Exception ex) {
//            response = new Response("Danh sách tin học " + ex.getMessage(), 1);
//        }
//        return response;
//    }
//
//    //danh sách ngoại ngữ
//    public Response setNgoaingus(JSONArray ngoaingus, String madonvi, String nhansu_id) {
//        Response response = null;
//        try {
//            for (int i = 0; i < ngoaingus.length(); i++) {
//                JSONObject ngoaingu = ngoaingus.getJSONObject(i);
//                ngoaingu.put("NhanSu_Id", nhansu_id);
//                response = getstoreProcedure(_ngoaingu, madonvi, ngoaingu.toString());
//                if (response.getErr_code() == 1)
//                    break;
//            }
//        } catch (Exception ex) {
//            response = new Response("Danh sách ngoại ngữ " + ex.getMessage(), 1);
//        }
//        return response;
//    }
//
//    //quá trình đào tạo bồi dưỡng
//    public Response setQuatrinhdaotaoboiduongs(JSONArray quatrinhdaotaobuoiduongs, String madonvi, String nhansu_id) {
//        Response response = null;
//        try {
//            for (int i = 0; i < quatrinhdaotaobuoiduongs.length(); i++) {
//                JSONObject quatrinhdaotaobuoiduong = quatrinhdaotaobuoiduongs.getJSONObject(i);
//                quatrinhdaotaobuoiduong.put("NhanSu_Id", nhansu_id);
//                response = getstoreProcedure(_daotaoboiduong, madonvi, quatrinhdaotaobuoiduong.toString());
//                if (response.getErr_code() == 1)
//                    break;
//            }
//        } catch (Exception ex) {
//            response = new Response("Quá trình đào tạo bồi dưỡng " + ex.getMessage(), 1);
//        }
//        return response;
//    }
//
//    //thông tin khác
//    public Response setThongtinkhac(JSONObject thongtinkhac, String madonvi, String nhansu_id) {
//        Response response = null;
//        try {
//            thongtinkhac.put("NhanSu_Id", nhansu_id);
//            response = getstoreProcedure(_ns_thongtinkhac, madonvi, thongtinkhac.toString());
//        } catch (Exception ex) {
//            response = new Response("Thông tin khác " + ex.getMessage(), 1);
//        }
//        return response;
//    }
//
//    //kết quả đanh giá phân loại
//    public Response setKetquadanhgiaphanloais(JSONArray ketquadanhgiaphanloais, String madonvi, String nhansu_id) {
//        Response response = null;
//        try {
//            for (int i = 0; i < ketquadanhgiaphanloais.length(); i++) {
//                JSONObject ketquadanhgiaphanloai = ketquadanhgiaphanloais.getJSONObject(i);
//                ketquadanhgiaphanloai.put("NhanSu_Id", nhansu_id);
//                response = getstoreProcedure(_danhgiaphanloai, madonvi, ketquadanhgiaphanloai.toString());
//                if (response.getErr_code() == 1)
//                    break;
//            }
//        } catch (Exception ex) {
//            response = new Response("Đánh giá phân loại " + ex.getMessage(), 1);
//        }
//        return response;
//    }
//
//    //xóa hồ sơ nhân sự và các dữ liệu liên quan
//    public ResponseEntity delHosonhansu(Response response, String madonvi, String sohieucbccvc_bndp) {
//        try {
//            getstoreProcedure(_xoahosonhansu, madonvi, sohieucbccvc_bndp);
//            return ResponseEntity.ok(new Response(response.getMessage(), response.getErr_code()));
//        } catch (Exception ex) {
//            return ResponseEntity.ok(new Response("Xóa hồ sơ nhân sự " + ex.getMessage(), 1));
//        }
//    }
//    //kiểm tra số hiệu cbccvc_bndp
//    public Response selSohieuCbccvc_Bndp(String madonvi, String sohieucbccvc_bndp) {
//        Response response = null;
//        try {
//            response = getstoreProcedure(_sohieucbccvc_bndp, madonvi, sohieucbccvc_bndp);
//        } catch (Exception ex) {
//            response = new Response("Tìm kiếm hồ sơ cbccvc bndp " + ex.getMessage(), 1);
//        }
//        return  response;
//    }
//
//    // xử lý json trả về
//    public Response getstoreProcedure(String storename, String madonvi, String json) {
//        try {
//            JSONObject retjson = new JSONObject(syncMsgService.callStoreProcedure(storename, madonvi, json));
//            String mess = retjson.getString("MSG_TEXT");
//            int err = retjson.getInt("MSG_CODE");
//            String value = retjson.getString("VAL");
//            return new Response(mess, err == 1 ? 0 : 1,value);
//        } catch (Exception ex) {
//            return new Response(ex.getMessage(), 1);
//        }
//    }
    public Response getstoreProcedure(String json) {
        try {
            JSONObject retjson = new JSONObject(json);
            String mess = retjson.getString("MSG_TEXT");
            int err = retjson.getInt("MSG_CODE");
            String value = retjson.getString("VAL");
            return new Response(mess, err == 1 ? 0 : 1, value);
        } catch (Exception ex) {
            return new Response(ex.getMessage(), 1);
        }
    }
}
