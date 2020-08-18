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
    // @Autowired DataSource dataSource;
    @PersistenceContext
    private EntityManager entityManager;
    private static EntityManagerFactory factory;
    private static final String PERSISTENCE_UNIT_NAME = "transactions-optional";

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
    @RequestMapping("/ServiceM0001_01")
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
                return (ResponseEntity<?>) ResponseEntity.ok(new Response(response.getMessage(), response.getErr_code()));

            JSONObject Body = data.getJSONObject("Body");
            JSONObject hoso_cbccvc = Body.getJSONObject("HOSO_CBCCVC");

            JSONObject thongtinchung = hoso_cbccvc.getJSONObject("THONGTINCHUNG");
            //kiểm tra và tạo mã số hiệu cán bộ bộ ngành địa phương
            String soHieucbccvc = thongtinchung.getString("SoHieuCBCCVC");
            if (soHieucbccvc == null || soHieucbccvc.isEmpty())
                return (ResponseEntity<?>) ResponseEntity.ok(new Response("Thêm mới hồ sơ nhân sự không thành công: Không tìm thấy số hiệu CBCCVC ", 1));

            JSONObject sohieucbccvc_bndp = new JSONObject();
            String sohieubndp = madonvi + "_" + soHieucbccvc;
            sohieucbccvc_bndp.put("SoHieuCBCCVC_BNDP", sohieubndp);

            // thêm mới thông tin chung vào bảng nhân sự
            thongtinchung.put("SoHieuCBCCVC_BNDP", sohieubndp);
            response = getstoreProcedure(_ns_thongtinchung, madonvi, thongtinchung.toString());
            if (response.getErr_code() == 1)
                return (ResponseEntity<?>) ResponseEntity.ok(new Response(response.getMessage(), response.getErr_code()));
            else
                nhansu_id = response.getValue();


            JSONObject tuyendungquatrinhcongtac = hoso_cbccvc.getJSONObject("TUYENDUNG_QT_CONGTAC");
            tuyendungquatrinhcongtac.put("NhanSu_Id", nhansu_id);
            // cập nhật tuyển dụng quá trình công tác vào bảng nhân sự
            response = getstoreProcedure(_ns_tuyendungquatrinhcongtac, madonvi, tuyendungquatrinhcongtac.toString());
            if (response.getErr_code() == 1) {
                getstoreProcedure(_xoahosonhansu, madonvi, sohieucbccvc_bndp.toString());
                return (ResponseEntity<?>) ResponseEntity.ok(new Response(response.getMessage(), response.getErr_code()));
            }

            JSONArray quatrinhcongtacs = tuyendungquatrinhcongtac.getJSONArray("DS_QUATRINH_CONGTAC");
            for (int i = 0; i < quatrinhcongtacs.length(); i++) {
                JSONObject quatrinhcongtac = quatrinhcongtacs.getJSONObject(i);
                quatrinhcongtac.put("NhanSu_Id", nhansu_id);
                // thêm mới vào bảng quá trình công tác
                response = getstoreProcedure(_ns_quatrinhcongtac, madonvi, quatrinhcongtac.toString());
                if (response.getErr_code() == 1) {
                    getstoreProcedure(_xoahosonhansu, madonvi, sohieucbccvc_bndp.toString());
                    return (ResponseEntity<?>) ResponseEntity.ok(new Response(response.getMessage(), response.getErr_code()));
                }
            }

            JSONObject luongphucap = hoso_cbccvc.getJSONObject("LUONG_PHUCAP_CHUCVU");
            luongphucap.put("NhanSu_Id", nhansu_id);
            // cập nhật lương phục cấp vào bảng nhân sự
            response = getstoreProcedure(_ns_luongphucap, madonvi, luongphucap.toString());
            if (response.getErr_code() == 1) {
                getstoreProcedure(_xoahosonhansu, madonvi, sohieucbccvc_bndp.toString());
                return (ResponseEntity<?>) ResponseEntity.ok(new Response(response.getMessage(), response.getErr_code()));
            }

            JSONArray quatrinhphucaps = luongphucap.getJSONArray("DS_QUATRINH_PHUCAP");
            for (int i = 0; i < quatrinhphucaps.length(); i++) {
                JSONObject quatrinhphucap = quatrinhphucaps.getJSONObject(i);
                quatrinhphucap.put("NhanSu_Id", nhansu_id);
                // thêm mới vào bảng phụ cấp
                response = getstoreProcedure(_phucap, madonvi, quatrinhphucap.toString());
                if (response.getErr_code() == 1) {
                    getstoreProcedure(_xoahosonhansu, madonvi, sohieucbccvc_bndp.toString());
                    return (ResponseEntity<?>) ResponseEntity.ok(new Response(response.getMessage(), response.getErr_code()));
                }
            }

            JSONArray quatrinhluongs = luongphucap.getJSONArray("DS_QUATRINH_LUONG");
            for (int i = 0; i < quatrinhluongs.length(); i++) {
                JSONObject quatrinhluong = quatrinhluongs.getJSONObject(i);
                quatrinhluong.put("NhanSu_Id", nhansu_id);
                // thêm mới vào bảng luong
                response = getstoreProcedure(_luong, madonvi, quatrinhluong.toString());
                if (response.getErr_code() == 1) {
                    getstoreProcedure(_xoahosonhansu, madonvi, sohieucbccvc_bndp.toString());
                    return (ResponseEntity<?>) ResponseEntity.ok(new Response(response.getMessage(), response.getErr_code()));
                }
            }

            JSONObject daotaoboiduong = hoso_cbccvc.getJSONObject("DAOTAO_BOIDUONG");
            daotaoboiduong.put("NhanSu_Id", nhansu_id);
            // cập nhật đào tạo bồi dưỡng vào bảng nhân sự
            response = getstoreProcedure(_ns_daotaoboiduong, madonvi, daotaoboiduong.toString());
            if (response.getErr_code() == 1) {
                getstoreProcedure(_xoahosonhansu, madonvi, sohieucbccvc_bndp.toString());
                return (ResponseEntity<?>) ResponseEntity.ok(new Response(response.getMessage(), response.getErr_code()));
            }

            JSONArray tinhocs = daotaoboiduong.getJSONArray("DS_TINHOC");
            for (int i = 0; i < tinhocs.length(); i++) {
                JSONObject tinhoc = tinhocs.getJSONObject(i);
                tinhoc.put("NhanSu_Id", nhansu_id);
                // thêm mới vào bảng tin học
                response = getstoreProcedure(_tinhoc, madonvi, tinhoc.toString());
                if (response.getErr_code() == 1) {
                    getstoreProcedure(_xoahosonhansu, madonvi, sohieucbccvc_bndp.toString());
                    return (ResponseEntity<?>) ResponseEntity.ok(new Response(response.getMessage(), response.getErr_code()));
                }
            }

            JSONArray ngoaingus = daotaoboiduong.getJSONArray("DS_NGOAINGU");
            for (int i = 0; i < ngoaingus.length(); i++) {
                JSONObject ngoaingu = ngoaingus.getJSONObject(i);
                // thêm mới vào bảng ngoại ngữ
                ngoaingu.put("NhanSu_Id", nhansu_id);
                // thêm mới vào bảng tin học
                response = getstoreProcedure(_ngoaingu, madonvi, ngoaingu.toString());
                if (response.getErr_code() == 1) {
                    getstoreProcedure(_xoahosonhansu, madonvi, sohieucbccvc_bndp.toString());
                    return (ResponseEntity<?>) ResponseEntity.ok(new Response(response.getMessage(), response.getErr_code()));
                }
            }

            JSONArray quatrinhdaotaobuoiduongs = daotaoboiduong.getJSONArray("DS_QUATRINH_DAOTAO_BOIDUONG");
            for (int i = 0; i < quatrinhdaotaobuoiduongs.length(); i++) {
                JSONObject quatrinhdaotaobuoiduong = quatrinhdaotaobuoiduongs.getJSONObject(i);
                quatrinhdaotaobuoiduong.put("NhanSu_Id", nhansu_id);
                // thêm mới vào bảng quá trình đào tạo bồi dưỡng
                response = getstoreProcedure(_daotaoboiduong, madonvi, quatrinhdaotaobuoiduong.toString());
                if (response.getErr_code() == 1) {
                    getstoreProcedure(_xoahosonhansu, madonvi, sohieucbccvc_bndp.toString());
                    return (ResponseEntity<?>) ResponseEntity.ok(new Response(response.getMessage(), response.getErr_code()));
                }
            }

            JSONObject thongtinkhac = hoso_cbccvc.getJSONObject("THONGTIN_KHAC");
            thongtinkhac.put("NhanSu_Id", nhansu_id);
            // cập nhật thông tin vào bảng nhân sự
            response = getstoreProcedure(_ns_thongtinkhac, madonvi, thongtinkhac.toString());
            if (response.getErr_code() == 1) {
                getstoreProcedure(_xoahosonhansu, madonvi, sohieucbccvc_bndp.toString());
                return (ResponseEntity<?>) ResponseEntity.ok(new Response(response.getMessage(), response.getErr_code()));
            }

            JSONArray ketquadanhgiaphanloais = hoso_cbccvc.getJSONArray("DS_KETQUA_DANHGIA_PHANLOAI");
            for (int i = 0; i < ketquadanhgiaphanloais.length(); i++) {
                JSONObject ketquadanhgiaphanloai = ketquadanhgiaphanloais.getJSONObject(i);
                ketquadanhgiaphanloai.put("NhanSu_Id", nhansu_id);
                // thêm mới vào bảng đánh giá phân loại
                response = getstoreProcedure(_danhgiaphanloai, madonvi, ketquadanhgiaphanloai.toString());
                if (response.getErr_code() == 1) {
                    getstoreProcedure(_xoahosonhansu, madonvi, sohieucbccvc_bndp.toString());
                    return (ResponseEntity<?>) ResponseEntity.ok(new Response(response.getMessage(), response.getErr_code()));
                }
            }
            System.out.println("--------------------------------------------------------import hosonhansu");
            System.out.println("Thêm mới hồ sơ nhân sư thành công " + sohieubndp);
            return (ResponseEntity<?>) ResponseEntity.ok(new Response("Thêm mới hồ sơ nhân sư thành công", 0));

        } catch (Exception ex) {
            return (ResponseEntity<?>) ResponseEntity.ok(new Response("Thêm mới hồ sơ nhân sự không thành công: " + ex.getMessage(), 1));
        }
    }

    @RequestMapping("/ServiceM0001_011")
    public ResponseEntity<?> ServiceM0001_011(@RequestBody Map<String, String> body) {
        try {
            Response response = null;
            String nhansu_id = null;

            String madonvi = body.get("donviid");
            String strXml = body.get("xmlContent");
            byte[] decoded = Base64.decodeBase64(strXml);
            String strXmlDecode = new String(decoded, "UTF-8");

            JSONObject jsonObj = XML.toJSONObject(strXmlDecode, true);

            JSONObject data = jsonObj.getJSONObject("Data");
            JSONObject header = data.getJSONObject("Header");

            // thêm mới vào bảng header
            response = getstoreProcedure(_header, madonvi, header.toString());
            if (response.getErr_code() == 1)
                return (ResponseEntity<?>) ResponseEntity.ok(new Response(response.getMessage(), response.getErr_code()));

            JSONObject Body = data.getJSONObject("Body");
            JSONObject hoso_cbccvc = Body.getJSONObject("HOSO_CBCCVC");


            JSONObject thongtinchung = hoso_cbccvc.getJSONObject("THONGTINCHUNG");

            //kiểm tra và tạo mã số hiệu cán bộ bộ ngành địa phương
            String soHieucbccvc = thongtinchung.getString("SoHieuCBCCVC");
            if (soHieucbccvc == null || soHieucbccvc.isEmpty())
                return (ResponseEntity<?>) ResponseEntity.ok(new Response("Thêm mới hồ sơ nhân sự không thành công: Không tìm thấy số hiệu CBCCVC ", 1));

            JSONObject sohieucbccvc_bndp = new JSONObject();
            String sohieubndp = madonvi + "_" + soHieucbccvc;
            sohieucbccvc_bndp.put("SoHieuCBCCVC_BNDP", sohieubndp);

            // thêm mới thông tin chung vào bảng nhân sự
            thongtinchung.put("SoHieuCBCCVC_BNDP", sohieubndp);
            response = getstoreProcedure(_ns_thongtinchung, madonvi, thongtinchung.toString());
            if (response.getErr_code() == 1)
                return (ResponseEntity<?>) ResponseEntity.ok(new Response(response.getMessage(), response.getErr_code()));
            else
                nhansu_id = response.getValue();


            JSONObject tuyendungquatrinhcongtac = hoso_cbccvc.getJSONObject("TUYENDUNG_QT_CONGTAC");
            tuyendungquatrinhcongtac.put("NhanSu_Id", nhansu_id);
            // cập nhật tuyển dụng quá trình công tác vào bảng nhân sự
            response = getstoreProcedure(_ns_tuyendungquatrinhcongtac, madonvi, tuyendungquatrinhcongtac.toString());
            if (response.getErr_code() == 1) {
                getstoreProcedure(_xoahosonhansu, madonvi, sohieucbccvc_bndp.toString());
                return (ResponseEntity<?>) ResponseEntity.ok(new Response(response.getMessage(), response.getErr_code()));
            }


            JSONObject _quatrinhcongtacs = tuyendungquatrinhcongtac.getJSONObject("DS_QUATRINH_CONGTAC");
            Object intervention = _quatrinhcongtacs.get("QUATRINH_CONGTAC");
            JSONArray quatrinhcongtacs = new JSONArray();
            if (intervention instanceof JSONArray)
                quatrinhcongtacs = (JSONArray) intervention;
            else if (intervention instanceof JSONObject)
                quatrinhcongtacs.put((JSONObject) intervention);

            for (int i = 0; i < quatrinhcongtacs.length(); i++) {
                JSONObject quatrinhcongtac = quatrinhcongtacs.getJSONObject(i);
                quatrinhcongtac.put("NhanSu_Id", nhansu_id);
                // thêm mới vào bảng quá trình công tác
                response = getstoreProcedure(_ns_quatrinhcongtac, madonvi, quatrinhcongtac.toString());
                if (response.getErr_code() == 1) {
                    getstoreProcedure(_xoahosonhansu, madonvi, sohieucbccvc_bndp.toString());
                    return (ResponseEntity<?>) ResponseEntity.ok(new Response(response.getMessage(), response.getErr_code()));
                }
            }

            JSONObject luongphucap = hoso_cbccvc.getJSONObject("LUONG_PHUCAP_CHUCVU");
            luongphucap.put("NhanSu_Id", nhansu_id);
            // cập nhật lương phục cấp vào bảng nhân sự
            response = getstoreProcedure(_ns_luongphucap, madonvi, luongphucap.toString());
            if (response.getErr_code() == 1) {
                getstoreProcedure(_xoahosonhansu, madonvi, sohieucbccvc_bndp.toString());
                return (ResponseEntity<?>) ResponseEntity.ok(new Response(response.getMessage(), response.getErr_code()));
            }

            JSONObject _quatrinhphucaps = luongphucap.getJSONObject("DS_QUATRINH_PHUCAP");
            intervention = _quatrinhcongtacs.get("QUATRINH_PHUCAP");
            JSONArray quatrinhphucaps = new JSONArray();
            if (intervention instanceof JSONArray)
                quatrinhcongtacs = (JSONArray) intervention;
            else if (intervention instanceof JSONObject)
                quatrinhcongtacs.put((JSONObject) intervention);

            for (int i = 0; i < quatrinhphucaps.length(); i++) {
                JSONObject quatrinhphucap = quatrinhphucaps.getJSONObject(i);
                quatrinhphucap.put("NhanSu_Id", nhansu_id);
                // thêm mới vào bảng phụ cấp
                response = getstoreProcedure(_phucap, madonvi, quatrinhphucap.toString());
                if (response.getErr_code() == 1) {
                    getstoreProcedure(_xoahosonhansu, madonvi, sohieucbccvc_bndp.toString());
                    return (ResponseEntity<?>) ResponseEntity.ok(new Response(response.getMessage(), response.getErr_code()));
                }
            }

            JSONObject _quatrinhluongs = luongphucap.getJSONObject("DS_QUATRINH_LUONG");
            intervention = _quatrinhluongs.get("QUATRINH_LUONG");
            JSONArray quatrinhluongs = new JSONArray();
            if (intervention instanceof JSONArray)
                quatrinhluongs = (JSONArray) intervention;
            else if (intervention instanceof JSONObject)
                quatrinhluongs.put((JSONObject) intervention);
            for (int i = 0; i < quatrinhluongs.length(); i++) {
                JSONObject quatrinhluong = quatrinhluongs.getJSONObject(i);
                quatrinhluong.put("NhanSu_Id", nhansu_id);
                // thêm mới vào bảng luong
                response = getstoreProcedure(_luong, madonvi, quatrinhluong.toString());
                if (response.getErr_code() == 1) {
                    getstoreProcedure(_xoahosonhansu, madonvi, sohieucbccvc_bndp.toString());
                    return (ResponseEntity<?>) ResponseEntity.ok(new Response(response.getMessage(), response.getErr_code()));
                }
            }

            JSONObject daotaoboiduong = hoso_cbccvc.getJSONObject("DAOTAO_BOIDUONG");
            daotaoboiduong.put("NhanSu_Id", nhansu_id);
            // cập nhật đào tạo bồi dưỡng vào bảng nhân sự
            response = getstoreProcedure(_ns_daotaoboiduong, madonvi, daotaoboiduong.toString());
            if (response.getErr_code() == 1) {
                getstoreProcedure(_xoahosonhansu, madonvi, sohieucbccvc_bndp.toString());
                return (ResponseEntity<?>) ResponseEntity.ok(new Response(response.getMessage(), response.getErr_code()));
            }

            JSONObject _tinhocs = daotaoboiduong.getJSONObject("DS_TINHOC");
            intervention = _tinhocs.get("TINHOC");
            JSONArray tinhocs = new JSONArray();
            if (intervention instanceof JSONArray)
                tinhocs = (JSONArray) intervention;
            else if (intervention instanceof JSONObject)
                tinhocs.put((JSONObject) intervention);
            for (int i = 0; i < tinhocs.length(); i++) {
                JSONObject tinhoc = tinhocs.getJSONObject(i);
                tinhoc.put("NhanSu_Id", nhansu_id);
                // thêm mới vào bảng tin học
                response = getstoreProcedure(_tinhoc, madonvi, tinhoc.toString());
                if (response.getErr_code() == 1) {
                    getstoreProcedure(_xoahosonhansu, madonvi, sohieucbccvc_bndp.toString());
                    return (ResponseEntity<?>) ResponseEntity.ok(new Response(response.getMessage(), response.getErr_code()));
                }
            }

            JSONObject _ngoaingus = daotaoboiduong.getJSONObject("DS_NGOAINGU");
            intervention = _tinhocs.get("NGOAINGU");
            JSONArray ngoaingus = new JSONArray();
            if (intervention instanceof JSONArray)
                ngoaingus = (JSONArray) intervention;
            else if (intervention instanceof JSONObject)
                ngoaingus.put((JSONObject) intervention);
            for (int i = 0; i < ngoaingus.length(); i++) {
                JSONObject ngoaingu = ngoaingus.getJSONObject(i);
                // thêm mới vào bảng ngoại ngữ
                ngoaingu.put("NhanSu_Id", nhansu_id);
                // thêm mới vào bảng tin học
                response = getstoreProcedure(_ngoaingu, madonvi, ngoaingu.toString());
                if (response.getErr_code() == 1) {
                    getstoreProcedure(_xoahosonhansu, madonvi, sohieucbccvc_bndp.toString());
                    return (ResponseEntity<?>) ResponseEntity.ok(new Response(response.getMessage(), response.getErr_code()));
                }
            }

            JSONObject _quatrinhdaotaobuoiduongs = daotaoboiduong.getJSONObject("DS_QUATRINH_DAOTAO_BOIDUONG");
            intervention = _quatrinhdaotaobuoiduongs.get("QUATRINH_DAOTAO_BOIDUONG");
            JSONArray quatrinhdaotaobuoiduongs = new JSONArray();
            if (intervention instanceof JSONArray)
                quatrinhdaotaobuoiduongs = (JSONArray) intervention;
            else if (intervention instanceof JSONObject)
                quatrinhdaotaobuoiduongs.put((JSONObject) intervention);
            for (int i = 0; i < quatrinhdaotaobuoiduongs.length(); i++) {
                JSONObject quatrinhdaotaobuoiduong = quatrinhdaotaobuoiduongs.getJSONObject(i);
                quatrinhdaotaobuoiduong.put("NhanSu_Id", nhansu_id);
                // thêm mới vào bảng quá trình đào tạo bồi dưỡng
                response = getstoreProcedure(_daotaoboiduong, madonvi, quatrinhdaotaobuoiduong.toString());
                if (response.getErr_code() == 1) {
                    getstoreProcedure(_xoahosonhansu, madonvi, sohieucbccvc_bndp.toString());
                    return (ResponseEntity<?>) ResponseEntity.ok(new Response(response.getMessage(), response.getErr_code()));
                }
            }

            JSONObject thongtinkhac = hoso_cbccvc.getJSONObject("THONGTIN_KHAC");
            thongtinkhac.put("NhanSu_Id", nhansu_id);
            // cập nhật thông tin vào bảng nhân sự
            response = getstoreProcedure(_ns_thongtinkhac, madonvi, thongtinkhac.toString());
            if (response.getErr_code() == 1) {
                getstoreProcedure(_xoahosonhansu, madonvi, sohieucbccvc_bndp.toString());
                return (ResponseEntity<?>) ResponseEntity.ok(new Response(response.getMessage(), response.getErr_code()));
            }

            JSONObject _ketquadanhgiaphanloais = hoso_cbccvc.getJSONObject("DS_KETQUA_DANHGIA_PHANLOAI");
            intervention = _ketquadanhgiaphanloais.get("QUATRINH_DAOTAO_BOIDUONG");
            JSONArray ketquadanhgiaphanloais = new JSONArray();
            if (intervention instanceof JSONArray)
                ketquadanhgiaphanloais = (JSONArray) intervention;
            else if (intervention instanceof JSONObject)
                ketquadanhgiaphanloais.put((JSONObject) intervention);
            for (int i = 0; i < ketquadanhgiaphanloais.length(); i++) {
                JSONObject ketquadanhgiaphanloai = ketquadanhgiaphanloais.getJSONObject(i);
                ketquadanhgiaphanloai.put("NhanSu_Id", nhansu_id);
                // thêm mới vào bảng đánh giá phân loại
                response = getstoreProcedure(_danhgiaphanloai, madonvi, ketquadanhgiaphanloai.toString());
                if (response.getErr_code() == 1) {
                    getstoreProcedure(_xoahosonhansu, madonvi, sohieucbccvc_bndp.toString());
                    return (ResponseEntity<?>) ResponseEntity.ok(new Response(response.getMessage(), response.getErr_code()));
                }
            }
            System.out.println("--------------------------------------------------------import hosonhansu");
            System.out.println("Thêm mới hồ sơ nhân sư thành công " + sohieubndp);
            return (ResponseEntity<?>) ResponseEntity.ok(new Response("Thêm mới hồ sơ nhân sư thành công", 0));

        } catch (Exception ex) {
            return (ResponseEntity<?>) ResponseEntity.ok(new Response("Thêm mới hồ sơ nhân sự không thành công: " + ex.getMessage(), 1));
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
