package com.bnv.controller;

import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;

import com.bnv.model.Response;
import com.bnv.repository.AdmUserRepository;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ApiSyncMsg/SyncMsg")
public class MsgController {
    @Autowired
    AdmUserRepository admUserRepository;

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

    // Thông điệp thêm mới hồ sơ CBCCVC vào hệ thống
    @RequestMapping("/ServiceM0001")
    public ResponseEntity<?> ServiceM0001_01(@RequestBody Map<String, String> body) {
        try {

            Response response = null;
            String nhansu_id = null;
            String madonvi = body.get("org_Code");
            String strJson = body.get("jsonContent");
            byte[] decoded = Base64.decodeBase64(strJson);
            String strJsonDecode = new String(decoded, "UTF-8");

            JSONObject jsonObj = new JSONObject(strJsonDecode);
            JSONObject data = jsonObj.getJSONObject("Data");
            JSONObject header = data.getJSONObject("Header");

            // thêm mới vào bảng header
            response = getstoreProcedure(_header, madonvi, header.toString());
            if (response.getErr_code() == 1)
                return ResponseEntity.ok(new Response(response.getMessage(), response.getErr_code()));

            JSONObject Body = data.getJSONObject("Body");
            JSONObject hoso_cbccvc = Body.getJSONObject("HOSO_CBCCVC");

            JSONObject thongtinchung = hoso_cbccvc.getJSONObject("THONGTINCHUNG");

            //kiểm tra và tạo mã số hiệu cán bộ bộ ngành địa phương
            String soHieucbccvc = thongtinchung.getString("SoHieuCBCCVC_BNDP");
            if (soHieucbccvc == null || soHieucbccvc.isEmpty())
                return ResponseEntity.ok(new Response("Thêm mới hồ sơ nhân sự không thành công: Không tìm thấy số hiệu CBCCVC ", 1));

            JSONObject sohieucbccvc_bndp = new JSONObject();
            String sohieubndp = madonvi + "_" + soHieucbccvc;
            sohieucbccvc_bndp.put("SoHieuCBCCVC_BNDP", sohieubndp);

            // thêm mới thông tin chung vào bảng nhân sự
            thongtinchung.put("SoHieuCBCCVC_BNDP", sohieubndp);
            response = getstoreProcedure(_ns_thongtinchung, madonvi, thongtinchung.toString());
            if (response.getErr_code() == 1)
                return ResponseEntity.ok(new Response(response.getMessage(), response.getErr_code()));
            else
                nhansu_id = response.getValue();


            JSONObject tuyendungquatrinhcongtac = hoso_cbccvc.getJSONObject("TUYENDUNG_QT_CONGTAC");
            response = setTuyendungquatrinhcongtac(tuyendungquatrinhcongtac, madonvi, nhansu_id);
            if (response.getErr_code() == 1)
                return delHosonhansu(response, madonvi, sohieucbccvc_bndp.toString());

            JSONArray quatrinhcongtacs = tuyendungquatrinhcongtac.getJSONArray("DS_QUATRINH_CONGTAC");
            response = setQuatrinhcongtacs(quatrinhcongtacs, madonvi, nhansu_id);
            if (response.getErr_code() == 1)
                return delHosonhansu(response, madonvi, sohieucbccvc_bndp.toString());

            JSONObject luongphucapchucvu = hoso_cbccvc.getJSONObject("LUONG_PHUCAP_CHUCVU");
            response = setLuongphucapchucvu(luongphucapchucvu, madonvi, nhansu_id);
            if (response.getErr_code() == 1)
                return delHosonhansu(response, madonvi, sohieucbccvc_bndp.toString());

            JSONArray quatrinhphucaps = luongphucapchucvu.getJSONArray("DS_QUATRINH_PHUCAP");
            response = setQuatrinhphucap(quatrinhphucaps, madonvi, nhansu_id);
            if (response.getErr_code() == 1)
                return delHosonhansu(response, madonvi, sohieucbccvc_bndp.toString());

            JSONArray quatrinhluongs = luongphucapchucvu.getJSONArray("DS_QUATRINH_LUONG");
            response = setQuatrinhluong(quatrinhluongs, madonvi, nhansu_id);
            if (response.getErr_code() == 1)
                return delHosonhansu(response, madonvi, sohieucbccvc_bndp.toString());

            JSONObject daotaoboiduong = hoso_cbccvc.getJSONObject("DAOTAO_BOIDUONG");
            response = setDaotaoboiduong(daotaoboiduong, madonvi, nhansu_id);
            if (response.getErr_code() == 1)
                return delHosonhansu(response, madonvi, sohieucbccvc_bndp.toString());

            JSONArray tinhocs = daotaoboiduong.getJSONArray("DS_TINHOC");
            response = setTinhocs(tinhocs, madonvi, nhansu_id);
            if (response.getErr_code() == 1)
                return delHosonhansu(response, madonvi, sohieucbccvc_bndp.toString());

            JSONArray ngoaingus = daotaoboiduong.getJSONArray("DS_NGOAINGU");
            response = setNgoaingus(ngoaingus, madonvi, nhansu_id);
            if (response.getErr_code() == 1)
                return delHosonhansu(response, madonvi, sohieucbccvc_bndp.toString());

            JSONArray quatrinhdaotaobuoiduongs = daotaoboiduong.getJSONArray("DS_QUATRINH_DAOTAO_BOIDUONG");
            response = setQuatrinhdaotaoboiduongs(quatrinhdaotaobuoiduongs, madonvi, nhansu_id);
            if (response.getErr_code() == 1)
                return delHosonhansu(response, madonvi, sohieucbccvc_bndp.toString());

            JSONObject thongtinkhac = hoso_cbccvc.getJSONObject("THONGTIN_KHAC");
            response = setThongtinkhac(thongtinkhac, madonvi, nhansu_id);
            if (response.getErr_code() == 1)
                return delHosonhansu(response, madonvi, sohieucbccvc_bndp.toString());

            JSONArray ketquadanhgiaphanloais = hoso_cbccvc.getJSONArray("DS_KETQUA_DANHGIA_PHANLOAI");
            response = setKetquadanhgiaphanloais(ketquadanhgiaphanloais, madonvi, nhansu_id);
            if (response.getErr_code() == 1)
                return delHosonhansu(response, madonvi, sohieucbccvc_bndp.toString());

            System.out.println("--------------------------------------------------------import hosonhansu");
            System.out.println("Thêm mới hồ sơ nhân sư thành công " + sohieubndp);
            return ResponseEntity.ok(new Response("Thêm mới hồ sơ nhân sư thành công", 0));

        } catch (Exception ex) {
            return ResponseEntity.ok(new Response("Thêm mới hồ sơ nhân sự không thành công: " + ex.getMessage(), 1));
        }
    }

    //tuyển dụng, quá trình công tác
    private Response setTuyendungquatrinhcongtac(JSONObject tuyendungquatrinhcongtac, String madonvi, String nhansu_id) {
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
    private Response setQuatrinhcongtacs(JSONArray quatrinhcongtacs, String madonvi, String nhansu_id) {
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
    private Response setLuongphucapchucvu(JSONObject luongphucapchucvu, String madonvi, String nhansu_id) {
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
    private Response setQuatrinhphucap(JSONArray quatrinhphucaps, String madonvi, String nhansu_id) {
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
    private Response setQuatrinhluong(JSONArray quatrinhluongs, String madonvi, String nhansu_id) {
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
    private Response setDaotaoboiduong(JSONObject daotaoboiduong, String madonvi, String nhansu_id) {
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
    private Response setTinhocs(JSONArray tinhocs, String madonvi, String nhansu_id) {
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
    private Response setNgoaingus(JSONArray ngoaingus, String madonvi, String nhansu_id) {
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
    private Response setQuatrinhdaotaoboiduongs(JSONArray quatrinhdaotaobuoiduongs, String madonvi, String nhansu_id) {
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
    private Response setThongtinkhac(JSONObject thongtinkhac, String madonvi, String nhansu_id) {
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
    private Response setKetquadanhgiaphanloais(JSONArray ketquadanhgiaphanloais, String madonvi, String nhansu_id) {
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
    private ResponseEntity delHosonhansu(Response response, String madonvi, String sohieucbccvc_bndp) {
        try {
            getstoreProcedure(_xoahosonhansu, madonvi, sohieucbccvc_bndp);
            return ResponseEntity.ok(new Response(response.getMessage(), response.getErr_code()));
        } catch (Exception ex) {
            return ResponseEntity.ok(new Response("Xóa hồ sơ nhân sự " + ex.getMessage(), 1));
        }
    }

    // xử lý json trả về
    private Response getstoreProcedure(String storename, String madonvi, String json) {
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
