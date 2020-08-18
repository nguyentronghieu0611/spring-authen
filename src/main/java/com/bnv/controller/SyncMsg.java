package com.bnv.controller;

import com.bnv.model.Response;
import com.bnv.repository.AdmUserRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;

public class SyncMsg {
    private static String _header = "pkg_parse_json.parse_header_json";
    private static String _ns_thongtinchung = "pkg_parse_json.parse_ns_thongtinchung_json";
    private static String _ns_tuyendungquatrinhcongtac = "pkg_parse_json.parse_ns_tuyendung_qtct_json";
    private static String _ns_quatrinhcongtac = "pkg_parse_json.parse_ns_quatrinhcongtac_json";
    private static String _ns_luongphucap = "pkg_parse_json.parse_ns_luongphucap_json";
    private static String _luong = "pkg_parse_json.parse_ns_luong_json";
    private static String _phucap = "pkg_parse_json.parse_ns_phucap_json";
    private static String _ns_daotaoboiduong = "pkg_parse_json.parse_ns_daotaoboiduong_json";
    private static String _daotaoboiduong = "pkg_parse_json.parse_daotaoboiduong_json";
    private static String _tinhoc = "pkg_parse_json.parse_tinhoc_json";
    private static String _ngoaingu = "pkg_parse_json.parse_ngoaingu_json";
    private static String _ns_thongtinkhac = "pkg_parse_json.parse_ns_thongtinkhac_json";
    private static String _danhgiaphanloai = "pkg_parse_json.parse_danhgiaphanloai_json";
    private static String _xoahosonhansu = "pkg_parse_json.parse_ns_xoahosonhansu_json";

    //thông tin chung
    public static Response setThongtinchung(JSONObject thongtinchung, String madonvi, String sohieucbccvc_bndp) {
        Response response = null;
        try {
            thongtinchung.put("SoHieuCBCCVC_BNDP", sohieucbccvc_bndp);
            response = getstoreProcedure(_ns_thongtinchung, madonvi, thongtinchung.toString());
        } catch (Exception ex) {
            response = new Response("Thông tin chung " + ex.getMessage(), 1);
        }
        return response;
    }

    //tuyển dụng, quá trình công tác
    public static Response setTuyendungquatrinhcongtac(JSONObject tuyendungquatrinhcongtac, String madonvi, String nhansu_id) {
        Response response = null;
        try {
            tuyendungquatrinhcongtac.put("NhanSu_Id", nhansu_id);
            response = getstoreProcedure(_ns_tuyendungquatrinhcongtac, madonvi, tuyendungquatrinhcongtac.toString());
        } catch (Exception ex) {
            response = new Response("Quá trình đào tạo bồi dưỡng " + ex.getMessage(), 1);
        }
        return response;
    }

    //quá trình công tác
    public static Response setQuatrinhcongtacs(JSONArray quatrinhcongtacs, String madonvi, String nhansu_id) {
        Response response = null;
        try {
            for (int i = 0; i < quatrinhcongtacs.length(); i++) {
                JSONObject quatrinhcongtac = quatrinhcongtacs.getJSONObject(i);
                quatrinhcongtac.put("NhanSu_Id", nhansu_id);
                response = getstoreProcedure(_ns_quatrinhcongtac, madonvi, quatrinhcongtac.toString());
                if (response.getErr_code() == 1)
                    break;
            }
        } catch (Exception ex) {
            response = new Response("Danh sách lương " + ex.getMessage(), 1);
        }
        return response;
    }

    //luong phụ cấp chức vụ
    public static Response setLuongphucapchucvu(JSONObject luongphucapchucvu, String madonvi, String nhansu_id) {
        Response response = null;
        try {
            luongphucapchucvu.put("NhanSu_Id", nhansu_id);
            response = getstoreProcedure(_ns_luongphucap, madonvi, luongphucapchucvu.toString());
        } catch (Exception ex) {
            response = new Response("Quá trình đào tạo bồi dưỡng " + ex.getMessage(), 1);
        }
        return response;
    }

    //quá trình phụ cấp
    public static Response setQuatrinhphucap(JSONArray quatrinhphucaps, String madonvi, String nhansu_id) {
        Response response = null;
        try {
            for (int i = 0; i < quatrinhphucaps.length(); i++) {
                JSONObject quatrinhphucap = quatrinhphucaps.getJSONObject(i);
                quatrinhphucap.put("NhanSu_Id", nhansu_id);
                response = getstoreProcedure(_phucap, madonvi, quatrinhphucap.toString());
                if (response.getErr_code() == 1)
                    break;
            }
        } catch (Exception ex) {
            response = new Response("Danh sách lương " + ex.getMessage(), 1);
        }
        return response;
    }

    //qua trình lương
    public static Response setQuatrinhluong(JSONArray quatrinhluongs, String madonvi, String nhansu_id) {
        Response response = null;
        try {
            for (int i = 0; i < quatrinhluongs.length(); i++) {
                JSONObject quatrinhluong = quatrinhluongs.getJSONObject(i);
                quatrinhluong.put("NhanSu_Id", nhansu_id);
                response = getstoreProcedure(_luong, madonvi, quatrinhluong.toString());
                if (response.getErr_code() == 1)
                    break;
            }
        } catch (Exception ex) {
            response = new Response("Danh sách lương " + ex.getMessage(), 1);
        }
        return response;
    }

    //đào tạo bồi dưỡng
    public static Response setDaotaoboiduong(JSONObject daotaoboiduong, String madonvi, String nhansu_id) {
        Response response = null;
        try {
            daotaoboiduong.put("NhanSu_Id", nhansu_id);
            response = getstoreProcedure(_ns_daotaoboiduong, madonvi, daotaoboiduong.toString());
        } catch (Exception ex) {
            response = new Response("Quá trình đào tạo bồi dưỡng " + ex.getMessage(), 1);
        }
        return response;
    }

    //danh sách tin học
    public static Response setTinhocs(JSONArray tinhocs, String madonvi, String nhansu_id) {
        Response response = null;
        try {
            for (int i = 0; i < tinhocs.length(); i++) {
                JSONObject tinhoc = tinhocs.getJSONObject(i);
                tinhoc.put("NhanSu_Id", nhansu_id);
                response = getstoreProcedure(_tinhoc, madonvi, tinhoc.toString());
                if (response.getErr_code() == 1)
                    break;
            }
        } catch (Exception ex) {
            response = new Response("Danh sách tin học " + ex.getMessage(), 1);
        }
        return response;
    }

    //danh sách ngoại ngữ
    public static Response setNgoaingus(JSONArray ngoaingus, String madonvi, String nhansu_id) {
        Response response = null;
        try {
            for (int i = 0; i < ngoaingus.length(); i++) {
                JSONObject ngoaingu = ngoaingus.getJSONObject(i);
                ngoaingu.put("NhanSu_Id", nhansu_id);
                response = getstoreProcedure(_ngoaingu, madonvi, ngoaingu.toString());
                if (response.getErr_code() == 1)
                    break;
            }
        } catch (Exception ex) {
            response = new Response("Danh sách ngoại ngữ " + ex.getMessage(), 1);
        }
        return response;
    }

    //quá trình đào tạo bồi dưỡng
    public static Response setQuatrinhdaotaoboiduongs(JSONArray quatrinhdaotaobuoiduongs, String madonvi, String nhansu_id) {
        Response response = null;
        try {
            for (int i = 0; i < quatrinhdaotaobuoiduongs.length(); i++) {
                JSONObject quatrinhdaotaobuoiduong = quatrinhdaotaobuoiduongs.getJSONObject(i);
                quatrinhdaotaobuoiduong.put("NhanSu_Id", nhansu_id);
                response = getstoreProcedure(_daotaoboiduong, madonvi, quatrinhdaotaobuoiduong.toString());
                if (response.getErr_code() == 1)
                    break;
            }
        } catch (Exception ex) {
            response = new Response("Quá trình đào tạo bồi dưỡng " + ex.getMessage(), 1);
        }
        return response;
    }

    //thông tin khác
    public static Response setThongtinkhac(JSONObject thongtinkhac, String madonvi, String nhansu_id) {
        Response response = null;
        try {
            thongtinkhac.put("NhanSu_Id", nhansu_id);
            response = getstoreProcedure(_ns_thongtinkhac, madonvi, thongtinkhac.toString());
        } catch (Exception ex) {
            response = new Response("Thông tin khác " + ex.getMessage(), 1);
        }
        return response;
    }

    //kết quả đanh giá phân loại
    public static Response setKetquadanhgiaphanloais(JSONArray ketquadanhgiaphanloais, String madonvi, String nhansu_id) {
        Response response = null;
        try {
            for (int i = 0; i < ketquadanhgiaphanloais.length(); i++) {
                JSONObject ketquadanhgiaphanloai = ketquadanhgiaphanloais.getJSONObject(i);
                ketquadanhgiaphanloai.put("NhanSu_Id", nhansu_id);
                response = getstoreProcedure(_danhgiaphanloai, madonvi, ketquadanhgiaphanloai.toString());
                if (response.getErr_code() == 1)
                    break;
            }
        } catch (Exception ex) {
            response = new Response("Đánh giá phân loại " + ex.getMessage(), 1);
        }
        return response;
    }

    //xóa hồ sơ nhân sự và các dữ liệu liên quan
    public static ResponseEntity delHosonhansu(Response response, String madonvi, String sohieucbccvc_bndp) {
        try {
            getstoreProcedure(_xoahosonhansu, madonvi, sohieucbccvc_bndp);
            return ResponseEntity.ok(new Response(response.getMessage(), response.getErr_code()));
        } catch (Exception ex) {
            return ResponseEntity.ok(new Response("Xóa hồ sơ nhân sự " + ex.getMessage(), 1));
        }
    }

    // xử lý json trả về
    public static Response getstoreProcedure(String storename, String madonvi, String json) {
        try {
            JSONObject retjson = new JSONObject(AdmUserRepository.callStoreProcedure(storename, madonvi, json));
            String mess = retjson.getString("MSG_TEXT");
            int err = retjson.getInt("MSG_CODE");
            String value = retjson.getString("VAL");
            return new Response(mess, err == 1 ? 0 : 1);
        } catch (Exception ex) {
            return new Response(ex.getMessage(), 1);
        }
    }
}