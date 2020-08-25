package com.bnv.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.bnv.config.JwtTokenUtil;
import com.bnv.model.Response;
import com.bnv.repository.AdmUserRepository;
import com.bnv.service.JwtUserDetailsService;
import com.bnv.service.SyncMsgService;
import com.bnv.util.ApplicationUtil;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/apisyncmsg/syncmsg")
public class MsgController {
    @Autowired
    AdmUserRepository admUserRepository;
    @Autowired
    SyncMsgService syncMsg;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;
    @Autowired
    private ApplicationUtil applicationUtil;

    @Value("#{'${dm.org_Code}'.split(',')}")
    private List<String> dmorg_Code;

    // Thông điệp thêm mới hồ sơ CBCCVC vào hệ thống
    @RequestMapping(value = {"/servicem0001", "/servicem0001_1"})
    public ResponseEntity<?> ServiceM0001(@RequestHeader("Authorization") String token, @RequestBody Map<String, String> body) {
        try {
            Response response = null;
            JSONObject objthongtinchung, objtuyendungquatrinhcongtac, objluongphucapchucvu, objtrinhdodaotaoboiduong, objthongtinkhac;
            JSONArray arrquatrinhcongtac = null, arrquatrinhphucap = null, arrquatrinhluong = null, arrtinhoc = null, arrngoaingu = null, arrquatrinhdaotaoboiduong = null, arrketquadanhgia = null;

            response = Header(token,body);
            if (response.getErr_code() == 1)
                return ResponseEntity.ok(new Response(response.getMessage(), response.getErr_code()));
            JSONObject _body = new JSONObject(response.getValue());
            int hosonhansu_id = _body.getInt("Nhansu_Id");
            String sohieubndp_bnv = _body.getString("SoHieuCBCCVC_BNDP");
            String action_type = _body.getString("Action_Type");
            JSONObject hoso_cbccvc = _body.getJSONObject("HOSO_CBCCVC");

            //thông tin chung
            objthongtinchung = hoso_cbccvc.getJSONObject("THONGTINCHUNG");
            objthongtinchung.put("NhanSu_Id", hosonhansu_id);
            objthongtinchung.put("SoHieuCBCCVC_BNDP", sohieubndp_bnv);

            //tuyển dụng quá trình công tác
            if (!hoso_cbccvc.isNull("TUYENDUNG_QT_CONGTAC") && !hoso_cbccvc.getJSONObject("TUYENDUNG_QT_CONGTAC").isEmpty()) {
                objtuyendungquatrinhcongtac = hoso_cbccvc.getJSONObject("TUYENDUNG_QT_CONGTAC");
                if (!objtuyendungquatrinhcongtac.isNull("DS_QUATRINH_CONGTAC") && objtuyendungquatrinhcongtac.getJSONArray("DS_QUATRINH_CONGTAC").length() > 0)
                    arrquatrinhcongtac = objtuyendungquatrinhcongtac.getJSONArray("DS_QUATRINH_CONGTAC");
                else
                    arrquatrinhcongtac = null;
            } else objtuyendungquatrinhcongtac = null;

            //lương phụ cấp chức vụ
            if (!hoso_cbccvc.isNull("LUONG_PHUCAP_CHUCVU") && !hoso_cbccvc.getJSONObject("LUONG_PHUCAP_CHUCVU").isEmpty()) {
                objluongphucapchucvu = hoso_cbccvc.getJSONObject("LUONG_PHUCAP_CHUCVU");
                if (!objluongphucapchucvu.isNull("DS_QUATRINH_PHUCAP") && objluongphucapchucvu.getJSONArray("DS_QUATRINH_PHUCAP").length() > 0)
                    arrquatrinhphucap = objluongphucapchucvu.getJSONArray("DS_QUATRINH_PHUCAP");
                else arrquatrinhphucap = null;
                if (!objluongphucapchucvu.isNull("DS_QUATRINH_LUONG") && objluongphucapchucvu.getJSONArray("DS_QUATRINH_LUONG").length() > 0)
                    arrquatrinhluong = objluongphucapchucvu.getJSONArray("DS_QUATRINH_LUONG");
                else arrquatrinhluong = null;
            } else objluongphucapchucvu = null;

            //trinh độ đào tạo bồi dưỡng
            if (!hoso_cbccvc.isNull("TRINHDO_DAOTAO_BOIDUONG") && !hoso_cbccvc.getJSONObject("TRINHDO_DAOTAO_BOIDUONG").isEmpty()) {
                objtrinhdodaotaoboiduong = hoso_cbccvc.getJSONObject("TRINHDO_DAOTAO_BOIDUONG");
                if (!objtrinhdodaotaoboiduong.isNull("DS_TINHOC") && objtrinhdodaotaoboiduong.getJSONArray("DS_TINHOC").length() > 0)
                    arrtinhoc = objtrinhdodaotaoboiduong.getJSONArray("DS_TINHOC");
                else arrtinhoc = null;
                if (!objtrinhdodaotaoboiduong.isNull("DS_NGOAINGU") && objtrinhdodaotaoboiduong.getJSONArray("DS_NGOAINGU").length() > 0)
                    arrngoaingu = objtrinhdodaotaoboiduong.getJSONArray("DS_NGOAINGU");
                else arrngoaingu = null;
                if (!objtrinhdodaotaoboiduong.isNull("DS_QUATRINH_DAOTAO_BOIDUONG") && objtrinhdodaotaoboiduong.getJSONArray("DS_QUATRINH_DAOTAO_BOIDUONG").length() > 0)
                    arrquatrinhdaotaoboiduong = objtrinhdodaotaoboiduong.getJSONArray("DS_QUATRINH_DAOTAO_BOIDUONG");
                else arrquatrinhdaotaoboiduong = null;
            } else objtrinhdodaotaoboiduong = null;

            //thông tin khác
            if (!hoso_cbccvc.isNull("THONGTIN_KHAC") && !hoso_cbccvc.getJSONObject("THONGTIN_KHAC").isEmpty())
                objthongtinkhac = hoso_cbccvc.getJSONObject("THONGTIN_KHAC");
            else objthongtinkhac = null;

            //kết quả đánh giá xếp loại
            if (!hoso_cbccvc.isNull("DS_KETQUA_DANHGIA_PHANLOAI") && hoso_cbccvc.getJSONArray("DS_KETQUA_DANHGIA_PHANLOAI").length() > 0)
                arrketquadanhgia = hoso_cbccvc.getJSONArray("DS_KETQUA_DANHGIA_PHANLOAI");
            else arrketquadanhgia = null;

            //call stor cập nhật dữ liệu
            response = syncMsg.callStoreProcedureServicem0001(action_type, hosonhansu_id, objthongtinchung, objtuyendungquatrinhcongtac, objluongphucapchucvu, objtrinhdodaotaoboiduong, objthongtinkhac,
                    arrquatrinhcongtac, arrquatrinhphucap, arrquatrinhluong, arrtinhoc, arrngoaingu, arrquatrinhdaotaoboiduong, arrketquadanhgia);

            System.out.println("---------------------import hosonhansu");
            System.out.println("Thêm mới dữ liệu cán bộ, công chức, viên chức thành công " + sohieubndp_bnv);
            return ResponseEntity.ok(new Response(response.getMessage(), response.getErr_code()));
        } catch (JSONException ex) {
            System.out.printf("Cấu trúc gói tin json không đúng định dạng quy định: %s!", ex.getMessage());
            return ResponseEntity.ok(new Response("Cấu trúc gói tin json không đúng định dạng quy định!", 1));
        } catch (Exception ex) {
            System.out.printf("Cập nhật dữ liệu cán bộ, công chức, viên chức không thành công: %s!", ex.getMessage());
            return ResponseEntity.ok(new Response("Cập nhật dữ liệu cán bộ, công chức, viên chức không thành công. " + ex.getMessage(), 1));
        }
    }

    //Tuyển dụng quá trình công tác
    @RequestMapping("/servicem0002")
    public ResponseEntity<?> ServiceM0002(@RequestHeader("Authorization") String token,@RequestBody Map<String, String> body) {
        try {
            Response response = null;
            JSONObject objtuyendungquatrinhcongtac;
            JSONArray arrquatrinhcongtac = null;

            response = Header(token,body);
            if (response.getErr_code() == 1)
                return ResponseEntity.ok(new Response(response.getMessage(), response.getErr_code()));
            JSONObject _body = new JSONObject(response.getValue());
            int hosonhansu_id = _body.getInt("Nhansu_Id");
            String sohieubndp_bnv = _body.getString("SoHieuCBCCVC_BNDP");
            String action_type = _body.getString("Action_Type");
            JSONObject hoso_cbccvc = _body.getJSONObject("HOSO_CBCCVC");

            //tuyển dụng quá trình công tác
            if (!hoso_cbccvc.isNull("TUYENDUNG_QT_CONGTAC") && !hoso_cbccvc.getJSONObject("TUYENDUNG_QT_CONGTAC").isEmpty()) {
                objtuyendungquatrinhcongtac = hoso_cbccvc.getJSONObject("TUYENDUNG_QT_CONGTAC");
                if (!objtuyendungquatrinhcongtac.isNull("DS_QUATRINH_CONGTAC") && objtuyendungquatrinhcongtac.getJSONArray("DS_QUATRINH_CONGTAC").length() > 0)
                    arrquatrinhcongtac = objtuyendungquatrinhcongtac.getJSONArray("DS_QUATRINH_CONGTAC");
                else
                    arrquatrinhcongtac = null;
            } else objtuyendungquatrinhcongtac = null;

            //call stor cập nhật dữ liệu
            response = syncMsg.callStoreProcedureServicem0002(action_type, hosonhansu_id, objtuyendungquatrinhcongtac, arrquatrinhcongtac);

            System.out.println("---------------------import hosonhansu");
            System.out.println("Cập nhật thông tin về tuyển dụng quá trình công tác" + sohieubndp_bnv);
            return ResponseEntity.ok(new Response(response.getMessage(), response.getErr_code()));
        } catch (JSONException ex) {
            System.out.printf("Cấu trúc gói tin json không đúng định dạng quy định: %s!", ex.getMessage());
            return ResponseEntity.ok(new Response("Cấu trúc gói tin json không đúng định dạng quy định!", 1));
        } catch (Exception ex) {
            System.out.printf("Cập nhật thông tin về tuyển dụng, quá trình công tác không thành công: %s!", ex.getMessage());
            return ResponseEntity.ok(new Response("Cập nhật thông tin về tuyển dụng, quá trình công tác không thành công. " + ex.getMessage(), 1));
        }
    }

    //Lương phụ cấp chức vụ
    @RequestMapping("/servicem0003")
    public ResponseEntity<?> ServiceM0003(@RequestHeader("Authorization") String token,@RequestBody Map<String, String> body) {
        try {
            Response response = null;
            JSONObject objluongphucapchucvu;
            JSONArray arrquatrinhphucap = null, arrquatrinhluong = null;

            response = Header(token,body);
            if (response.getErr_code() == 1)
                return ResponseEntity.ok(new Response(response.getMessage(), response.getErr_code()));
            JSONObject _body = new JSONObject(response.getValue());
            int hosonhansu_id = _body.getInt("Nhansu_Id");
            String sohieubndp_bnv = _body.getString("SoHieuCBCCVC_BNDP");
            String action_type = _body.getString("Action_Type");
            JSONObject hoso_cbccvc = _body.getJSONObject("HOSO_CBCCVC");

            //lương phụ cấp chức vụ
            if (!hoso_cbccvc.isNull("LUONG_PHUCAP_CHUCVU") && !hoso_cbccvc.getJSONObject("LUONG_PHUCAP_CHUCVU").isEmpty()) {
                objluongphucapchucvu = hoso_cbccvc.getJSONObject("LUONG_PHUCAP_CHUCVU");
                if (!objluongphucapchucvu.isNull("DS_QUATRINH_PHUCAP") && objluongphucapchucvu.getJSONArray("DS_QUATRINH_PHUCAP").length() > 0)
                    arrquatrinhphucap = objluongphucapchucvu.getJSONArray("DS_QUATRINH_PHUCAP");
                else arrquatrinhphucap = null;
                if (!objluongphucapchucvu.isNull("DS_QUATRINH_LUONG") && objluongphucapchucvu.getJSONArray("DS_QUATRINH_LUONG").length() > 0)
                    arrquatrinhluong = objluongphucapchucvu.getJSONArray("DS_QUATRINH_LUONG");
                else arrquatrinhluong = null;
            } else objluongphucapchucvu = null;

            //call stor cập nhật dữ liệu
            response = syncMsg.callStoreProcedureServicem0003(action_type, hosonhansu_id, objluongphucapchucvu, arrquatrinhphucap, arrquatrinhluong);

            System.out.println("---------------------import hosonhansu");
            System.out.println("Cập nhật thông tin về lương, phụ cấp, chức vụ" + sohieubndp_bnv);
            return ResponseEntity.ok(new Response(response.getMessage(), response.getErr_code()));
        } catch (JSONException ex) {
            System.out.printf("Cấu trúc gói tin json không đúng định dạng quy định: %s!", ex.getMessage());
            return ResponseEntity.ok(new Response("Cấu trúc gói tin json không đúng định dạng quy định!", 1));
        } catch (Exception ex) {
            System.out.printf("Cập nhật thông tin về lương, phụ cấp, chức vụ không thành công: %s!", ex.getMessage());
            return ResponseEntity.ok(new Response("Cập nhật thông tin về lương, phụ cấp, chức vụ không thành công. " + ex.getMessage(), 1));
        }
    }

    //Trinh độ đào tạo bồi dưỡng
    @RequestMapping("/servicem0004")
    public ResponseEntity<?> ServiceM0004(@RequestHeader("Authorization") String token,@RequestBody Map<String, String> body) {
        try {
            Response response = null;
            JSONObject objtrinhdodaotaoboiduong;
            JSONArray arrtinhoc = null, arrngoaingu = null, arrquatrinhdaotaoboiduong = null;
            response = Header(token,body);
            if (response.getErr_code() == 1)
                return ResponseEntity.ok(new Response(response.getMessage(), response.getErr_code()));
            JSONObject _body = new JSONObject(response.getValue());
            int hosonhansu_id = _body.getInt("Nhansu_Id");
            String sohieubndp_bnv = _body.getString("SoHieuCBCCVC_BNDP");
            String action_type = _body.getString("Action_Type");
            JSONObject hoso_cbccvc = _body.getJSONObject("HOSO_CBCCVC");

            //trinh độ đào tạo bồi dưỡng
            if (!hoso_cbccvc.isNull("TRINHDO_DAOTAO_BOIDUONG") && !hoso_cbccvc.getJSONObject("TRINHDO_DAOTAO_BOIDUONG").isEmpty()) {
                objtrinhdodaotaoboiduong = hoso_cbccvc.getJSONObject("TRINHDO_DAOTAO_BOIDUONG");
                if (!objtrinhdodaotaoboiduong.isNull("DS_TINHOC") && objtrinhdodaotaoboiduong.getJSONArray("DS_TINHOC").length() > 0)
                    arrtinhoc = objtrinhdodaotaoboiduong.getJSONArray("DS_TINHOC");
                else arrtinhoc = null;
                if (!objtrinhdodaotaoboiduong.isNull("DS_NGOAINGU") && objtrinhdodaotaoboiduong.getJSONArray("DS_NGOAINGU").length() > 0)
                    arrngoaingu = objtrinhdodaotaoboiduong.getJSONArray("DS_NGOAINGU");
                else arrngoaingu = null;
                if (!objtrinhdodaotaoboiduong.isNull("DS_QUATRINH_DAOTAO_BOIDUONG") && objtrinhdodaotaoboiduong.getJSONArray("DS_QUATRINH_DAOTAO_BOIDUONG").length() > 0)
                    arrquatrinhdaotaoboiduong = objtrinhdodaotaoboiduong.getJSONArray("DS_QUATRINH_DAOTAO_BOIDUONG");
                else arrquatrinhdaotaoboiduong = null;
            } else objtrinhdodaotaoboiduong = null;

            //call stor cập nhật dữ liệu
            response = syncMsg.callStoreProcedureServicem0004(action_type, hosonhansu_id, objtrinhdodaotaoboiduong, arrtinhoc, arrngoaingu, arrquatrinhdaotaoboiduong);

            System.out.println("---------------------import hosonhansu");
            System.out.println("Cập nhật thông tin trình độ, đào tạo, bồi dưỡng " + sohieubndp_bnv);
            return ResponseEntity.ok(new Response(response.getMessage(), response.getErr_code()));
        } catch (JSONException ex) {
            System.out.printf("Cấu trúc gói tin json không đúng định dạng quy định: %s!", ex.getMessage());
            return ResponseEntity.ok(new Response("Cấu trúc gói tin json không đúng định dạng quy định!", 1));
        } catch (Exception ex) {
            System.out.printf("Cập nhật thông tin trình độ, đào tạo, bồi dưỡng không thành công: %s!", ex.getMessage());
            return ResponseEntity.ok(new Response("Cập nhật thông tin trình độ, đào tạo, bồi dưỡng không thành công. " + ex.getMessage(), 1));
        }
    }

    //Thông tin khác
    @RequestMapping("/servicem0005")
    public ResponseEntity<?> ServiceM0005(@RequestHeader("Authorization") String token,@RequestBody Map<String, String> body) {
        try {
            Response response = null;
            JSONObject objthongtinkhac;
            response = Header(token,body);
            if (response.getErr_code() == 1)
                return ResponseEntity.ok(new Response(response.getMessage(), response.getErr_code()));
            JSONObject _body = new JSONObject(response.getValue());
            int hosonhansu_id = _body.getInt("Nhansu_Id");
            String sohieubndp_bnv = _body.getString("SoHieuCBCCVC_BNDP");
            String action_type = _body.getString("Action_Type");
            JSONObject hoso_cbccvc = _body.getJSONObject("HOSO_CBCCVC");

            //thông tin khác
            if (!hoso_cbccvc.isNull("THONGTIN_KHAC") && !hoso_cbccvc.getJSONObject("THONGTIN_KHAC").isEmpty())
                objthongtinkhac = hoso_cbccvc.getJSONObject("THONGTIN_KHAC");
            else objthongtinkhac = null;

            //call stor cập nhật dữ liệu
            response = syncMsg.callStoreProcedureServicem0005(action_type, hosonhansu_id, objthongtinkhac);

            System.out.println("---------------------import hosonhansu");
            System.out.println("Cập nhật thông tin khác " + sohieubndp_bnv);
            return ResponseEntity.ok(new Response(response.getMessage(), response.getErr_code()));
        } catch (JSONException ex) {
            System.out.printf("Cấu trúc gói tin json không đúng định dạng quy định: %s!", ex.getMessage());
            return ResponseEntity.ok(new Response("Cấu trúc gói tin json không đúng định dạng quy định!", 1));
        } catch (Exception ex) {
            System.out.printf("Cập nhật thông tin khác không thành công: %s!", ex.getMessage());
            return ResponseEntity.ok(new Response("Cập nhật thông tin khác không thành công. " + ex.getMessage(), 1));
        }
    }

    //Kết quả đánh giá xếp loại
    @RequestMapping("/servicem0006")
    public ResponseEntity<?> ServiceM0006(@RequestHeader("Authorization") String token,@RequestBody Map<String, String> body) {
        try {
            Response response = null;
            JSONArray arrketquadanhgia;
            response = Header(token,body);
            if (response.getErr_code() == 1)
                return ResponseEntity.ok(new Response(response.getMessage(), response.getErr_code()));
            JSONObject _body = new JSONObject(response.getValue());
            int hosonhansu_id = _body.getInt("Nhansu_Id");
            String sohieubndp_bnv = _body.getString("SoHieuCBCCVC_BNDP");
            String action_type = _body.getString("Action_Type");
            JSONObject hoso_cbccvc = _body.getJSONObject("HOSO_CBCCVC");

            //kết quả đánh giá xếp loại
            if (!hoso_cbccvc.isNull("DS_KETQUA_DANHGIA_PHANLOAI") && hoso_cbccvc.getJSONArray("DS_KETQUA_DANHGIA_PHANLOAI").length() > 0)
                arrketquadanhgia = hoso_cbccvc.getJSONArray("DS_KETQUA_DANHGIA_PHANLOAI");
            else arrketquadanhgia = null;

            //call stor cập nhật dữ liệu
            response = syncMsg.callStoreProcedureServicem0006(action_type, hosonhansu_id, arrketquadanhgia);

            System.out.println("---------------------import hosonhansu");
            System.out.println("Cập nhật kết quả đánh giá, xếp loại " + sohieubndp_bnv);
            return ResponseEntity.ok(new Response(response.getMessage(), response.getErr_code()));
        } catch (JSONException ex) {
            System.out.printf("Cấu trúc gói tin json không đúng định dạng quy định: %s!", ex.getMessage());
            return ResponseEntity.ok(new Response("Cấu trúc gói tin json không đúng định dạng quy định!", 1));
        } catch (Exception ex) {
            System.out.printf("Cập nhật kết quả đánh giá, xếp loại không thành công: %s!", ex.getMessage());
            return ResponseEntity.ok(new Response("Cập nhật kết quả đánh giá, xếp loại không thành công. " + ex.getMessage(), 1));
        }
    }

    //ghi thông tin giao dịch API
    private Response Header(String token, Map<String, String> body) {
        try {
            if(!applicationUtil.checkORGCODE(token,body))
                return new Response("Mã đơn vị không thuộc về tài khoản này",1);

            String madonvi = body.get("org_Code");
            if (!syncMsg.seachDanhmuc(dmorg_Code, madonvi))
                return new Response(String.format("Mã đơn vị %s không tồn tại trên hệ thống!", madonvi), 1);
            String strJson = body.get("jsonContent");
            byte[] decoded = Base64.decodeBase64(strJson);
            String strJsonDecode = new String(decoded, "UTF-8");

            JSONObject jsonObj = new JSONObject(strJsonDecode);
            JSONObject data = jsonObj.getJSONObject("Data");

            JSONObject objheader = data.getJSONObject("Header");
            if (!checkActionType(objheader))
                return new Response("Không tìm thấy hành động của hàm hoặc không thuộc ADD, DEL, EDIT!", 1);
            syncMsg.callStoreProcedureHeader(objheader);

            String action_type = objheader.getString("Action_Type");
            JSONObject Body = data.getJSONObject("Body");
            JSONObject hoso_cbccvc = Body.getJSONObject("HOSO_CBCCVC");

            if (hoso_cbccvc.isNull("SoHieuCBCCVC_BNDP") || hoso_cbccvc.getString("SoHieuCBCCVC_BNDP").isEmpty())
                return new Response("Không tìm thấy số hiệu CBCCVC Bộ ngành địa phương!", 1);

            JSONObject jsonSohieucbccvc_bndp = new JSONObject();
            String sohieubndp_bnv = String.format("%s_%s", madonvi, hoso_cbccvc.getString("SoHieuCBCCVC_BNDP"));
            jsonSohieucbccvc_bndp.put("SoHieuCBCCVC_BNDP", sohieubndp_bnv);
            int hosonhansu_id = checkSoHieuCbccvcBndp(jsonSohieucbccvc_bndp);
            if (hosonhansu_id == -1)
                return new Response("Không tìm thấy số hiệu CBCCVC Bộ ngành địa phương!", 1);
            else if (action_type.equals("ADD") && hosonhansu_id > 0)
                return new Response("Số hiệu cán bộ, công chức, viên chức Bộ ngành địa phương đã tồn tại!", 1);
            else if ((action_type.equals("EDIT") || action_type.equals("DEL")) && hosonhansu_id <= 0)
                return new Response("Số hiệu cán bộ, công chức, viên chức Bộ ngành địa phương không tồn tại!", 1);
            JSONObject val = new JSONObject();
            val.put("Nhansu_Id", hosonhansu_id);
            val.put("SoHieuCBCCVC_BNDP", sohieubndp_bnv);
            val.put("HOSO_CBCCVC", hoso_cbccvc);
            val.put("Action_Type", action_type);
            return new Response("Thêm mới thông tin Header thành công", 0, val.toString());
        } catch (JSONException ex) {
            return new Response("Cấu trúc gói tin json không đúng định dạng quy định!", 1);
        } catch (Exception ex) {
            return new Response("Cập nhật dữ liệu cán bộ, công chức, viên chức không thành công. Header không đúng định dạng!" + ex.getMessage(), 1);
        }
    }

    //check hành động trọng json
    private boolean checkActionType(JSONObject header) {
        boolean status = true;
        List<String> listAction_Type = Arrays.asList(new String[]{"ADD", "EDIT", "DEL"});
        if (header.isNull("Action_Type") || header.getString("Action_Type").isEmpty())
            status = false;
        else if (!listAction_Type.contains(header.getString("Action_Type")))
            status = false;
        return status;
    }

    //check SoHieuCBCCVC_BNDP -1: không tìm thấy thẻ liệu trong thẻ, 0: không tìm thấy tồn tại, khác -1,0 : là số id bản ghi tìm được ,:
    private int checkSoHieuCbccvcBndp(JSONObject jsonSohieucbccvc) {
        Response response = null;
        int status;
        response = syncMsg.selSohieuCbccvc_Bndp("", jsonSohieucbccvc.toString());
        if (response.getErr_code() == 1)
            status = -1;
        else if (response.getValue().equals(""))
            status = 0;
        else
            status = Integer.parseInt(response.getValue());
        return status;
    }
}
