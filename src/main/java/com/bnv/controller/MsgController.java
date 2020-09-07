package com.bnv.controller;

import com.bnv.model.MsgResponse;
import com.bnv.model.Response;
import com.bnv.model.SyncResponse;
import com.bnv.repository.AdmUserRepository;
import com.bnv.service.SyncMsgService;
import com.bnv.service.SyncService;
import com.bnv.util.ApplicationUtil;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/apisyncmsg/syncmsg")
public class MsgController {
    @Autowired
    AdmUserRepository admUserRepository;
    @Autowired
    SyncMsgService syncMsg;
    @Autowired
    SyncService sync;
    @Autowired
    private ApplicationUtil applicationUtil;

    @Value("#{'${dm.org_Code}'.split(',')}")
    private List<String> dmorg_Code;

    private MsgResponse ServiceValidate(String token, Map<String, String> body) {
        String uniqueID = UUID.randomUUID().toString();
        try {
            logTransaction(body.toString(), uniqueID);

            if (!applicationUtil.validateTokenOrg(token, body))
                return new MsgResponse(String.format("Tài khoản chưa được phân quyền đồng bộ dữ liệu cho đơn vị này %s !", body.get("org_Code")), 1, uniqueID);

            String madonvi = body.get("org_Code");
            if (!sync.validateCategory(dmorg_Code, madonvi))
                return new MsgResponse(String.format("Mã đơn vị %s không tồn tại trên hệ thống!", madonvi), 1, uniqueID);

            String strJson = body.get("jsonContent");
            byte[] decoded = Base64.decodeBase64(strJson);
            String strJsonDecode = new String(decoded, "UTF-8");

            JSONObject jsonObj = new JSONObject(strJsonDecode);
            JSONObject data = jsonObj.getJSONObject("Data");

            JSONObject objheader = data.getJSONObject("Header");
            if (!checkActionType(objheader))
                return new MsgResponse("Không tìm thấy hành động của hàm hoặc không thuộc ADD, DEL, EDIT!", 1, uniqueID);

            String action_type = objheader.getString("Action_Type");
            JSONObject Body = data.getJSONObject("Body");
            JSONObject hoso_cbccvc = Body.getJSONObject("HOSO_CBCCVC");

            if (hoso_cbccvc.isNull("SoHieuCBCCVC_BNDP") || hoso_cbccvc.getString("SoHieuCBCCVC_BNDP").isEmpty())
                return new MsgResponse("Không tìm thấy số hiệu CBCCVC Bộ ngành địa phương!", 1, uniqueID);

            //sinh số hiệu cán bộ công chức viên chức bộ ngành địa phương
            JSONObject jsonSohieucbccvc_bndp = new JSONObject();
            String sohieubndp_bnv = String.format("%s_%s", madonvi, hoso_cbccvc.getString("SoHieuCBCCVC_BNDP"));

            jsonSohieucbccvc_bndp.put("SoHieuCBCCVC_BNDP", sohieubndp_bnv);
            int hosonhansu_id = validateSohieuCBCCVC_BNDP(jsonSohieucbccvc_bndp);
            if (hosonhansu_id == -1)
                return new MsgResponse("Không tìm thấy số hiệu CBCCVC Bộ ngành địa phương!", 1, uniqueID);
            else if (action_type.equals("ADD") && hosonhansu_id > 0)
                return new MsgResponse("Số hiệu CBCCVC Bộ ngành địa phương đã tồn tại!", 1, uniqueID);
            else if ((action_type.equals("EDIT") || action_type.equals("DEL")) && hosonhansu_id <= 0)
                return new MsgResponse("Số hiệu CBCCVC Bộ ngành địa phương không tồn tại!", 1, uniqueID);

            if (action_type.equals("ADD"))
                if (hoso_cbccvc.isNull("THONGTINCHUNG") || hoso_cbccvc.getJSONObject("THONGTINCHUNG").isEmpty())
                    return new MsgResponse("Không tìm thấy thông tin trong khi thực hiện thêm mới dữ liệu cán bộ, công chức, viên chứ!", 1, uniqueID);

            JSONObject val = new JSONObject();
            val.put("Transaction_ID", uniqueID);
            val.put("Action_Type", action_type);
            val.put("Nhansu_Id", hosonhansu_id);
            val.put("Header", objheader);
            val.put("SoHieuCBCCVC_BNDP", sohieubndp_bnv);
            val.put("HOSO_CBCCVC", hoso_cbccvc);

            return new MsgResponse("Thêm mới thông tin Header thành công", 0, val.toString());
        } catch (JSONException ex) {
            return new MsgResponse("Cấu trúc gói tin json header không đúng định dạng quy định!", 1, uniqueID);
        } catch (Exception ex) {
            return new MsgResponse("Xử lý thông tin header không thành công!" + ex.getMessage(), 1, uniqueID);
        }
    }

    // Thông điệp thêm mới hồ sơ CBCCVC vào hệ thống
    @RequestMapping(value = {"/servicem0001", "/servicem0001_1"})
    public ResponseEntity<?> ServiceM0001(@RequestHeader("Authorization") String token, @RequestBody Map<String, String> body) {
        try {
//            JSONObject jsonSubject = new JSONObject("{\"id\":1,\"name\":\"Lampshade\",\"price\":0}");
//            JSONObject jsonSchema = new JSONObject("{\"$schema\":\"http://json-schema.org/draft-04/schema#\",\"title\":\"Product\",\"description\":\"A product from the catalog\",\"type\":\"object\",\"properties\":{\"id\":{\"description\":\"The unique identifier for a product\",\"type\":\"integer\"},\"name\":{\"description\":\"Name of the product\",\"type\":\"string\"},\"price\":{\"type\":\"number\",\"minimum\":0,\"exclusiveMinimum\":true}},\"required\":[\"id\",\"name\",\"price\"]}");
//            Schema schema = SchemaLoader.load(jsonSchema);
//            schema.validate(jsonSubject);

            JSONObject objthongtinchung, objtuyendungquatrinhcongtac, objluongphucapchucvu, objtrinhdodaotaoboiduong, objthongtinkhac;
            JSONArray arrquatrinhcongtac = null, arrquatrinhphucap = null, arrquatrinhluong = null, arrtinhoc = null, arrngoaingu = null, arrquatrinhdaotaoboiduong = null, arrketquadanhgia = null;

            MsgResponse msgresponse = ServiceValidate(token, body);
            if (msgresponse.getErr_code() == 1)
                return ResponseEntity.ok(msgresponse);

            JSONObject validate = new JSONObject(msgresponse.getTransaction_id());
            int hosonhansu_id = validate.getInt("Nhansu_Id");
            String sohieubndp_bnv = validate.getString("SoHieuCBCCVC_BNDP");
            String action_type = validate.getString("Action_Type");
            String transaction_id = validate.getString("Transaction_ID");
            JSONObject objhosocbccvc = validate.getJSONObject("HOSO_CBCCVC");

            //thông tin header
            JSONObject objheader = validate.getJSONObject("Header");
            objheader.put("Transaction_ID", transaction_id);

            //thông tin chung
            objthongtinchung = objhosocbccvc.getJSONObject("THONGTINCHUNG");
            objthongtinchung.put("NhanSu_Id", hosonhansu_id);
            objthongtinchung.put("SoHieuCBCCVC_BNDP", sohieubndp_bnv);

            //tuyển dụng quá trình công tác
            if (!objhosocbccvc.isNull("TUYENDUNG_QT_CONGTAC") && !objhosocbccvc.getJSONObject("TUYENDUNG_QT_CONGTAC").isEmpty()) {
                objtuyendungquatrinhcongtac = objhosocbccvc.getJSONObject("TUYENDUNG_QT_CONGTAC");
                if (!objtuyendungquatrinhcongtac.isNull("DS_QUATRINH_CONGTAC") && objtuyendungquatrinhcongtac.getJSONArray("DS_QUATRINH_CONGTAC").length() > 0)
                    arrquatrinhcongtac = objtuyendungquatrinhcongtac.getJSONArray("DS_QUATRINH_CONGTAC");
                else
                    arrquatrinhcongtac = null;
            } else objtuyendungquatrinhcongtac = null;

            //lương phụ cấp chức vụ
            if (!objhosocbccvc.isNull("LUONG_PHUCAP_CHUCVU") && !objhosocbccvc.getJSONObject("LUONG_PHUCAP_CHUCVU").isEmpty()) {
                objluongphucapchucvu = objhosocbccvc.getJSONObject("LUONG_PHUCAP_CHUCVU");
                if (!objluongphucapchucvu.isNull("DS_QUATRINH_PHUCAP") && objluongphucapchucvu.getJSONArray("DS_QUATRINH_PHUCAP").length() > 0)
                    arrquatrinhphucap = objluongphucapchucvu.getJSONArray("DS_QUATRINH_PHUCAP");
                else arrquatrinhphucap = null;
                if (!objluongphucapchucvu.isNull("DS_QUATRINH_LUONG") && objluongphucapchucvu.getJSONArray("DS_QUATRINH_LUONG").length() > 0)
                    arrquatrinhluong = objluongphucapchucvu.getJSONArray("DS_QUATRINH_LUONG");
                else arrquatrinhluong = null;
            } else objluongphucapchucvu = null;

            //trinh độ đào tạo bồi dưỡng
            if (!objhosocbccvc.isNull("TRINHDO_DAOTAO_BOIDUONG") && !objhosocbccvc.getJSONObject("TRINHDO_DAOTAO_BOIDUONG").isEmpty()) {
                objtrinhdodaotaoboiduong = objhosocbccvc.getJSONObject("TRINHDO_DAOTAO_BOIDUONG");
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
            if (!objhosocbccvc.isNull("THONGTIN_KHAC") && !objhosocbccvc.getJSONObject("THONGTIN_KHAC").isEmpty())
                objthongtinkhac = objhosocbccvc.getJSONObject("THONGTIN_KHAC");
            else objthongtinkhac = null;

            //kết quả đánh giá xếp loại
            if (!objhosocbccvc.isNull("DS_KETQUA_DANHGIA_PHANLOAI") && objhosocbccvc.getJSONArray("DS_KETQUA_DANHGIA_PHANLOAI").length() > 0)
                arrketquadanhgia = objhosocbccvc.getJSONArray("DS_KETQUA_DANHGIA_PHANLOAI");
            else arrketquadanhgia = null;

            //call stor cập nhật dữ liệu
            SyncResponse syncResponse = syncMsg.callStoreProcedureServicem0001(action_type, hosonhansu_id, objheader, objthongtinchung, objtuyendungquatrinhcongtac, objluongphucapchucvu, objtrinhdodaotaoboiduong, objthongtinkhac,
                    arrquatrinhcongtac, arrquatrinhphucap, arrquatrinhluong, arrtinhoc, arrngoaingu, arrquatrinhdaotaoboiduong, arrketquadanhgia);

            System.out.println("---------------------import hosonhansu");
            System.out.println("Thêm mới dữ liệu CBCCVC thành công " + sohieubndp_bnv);
            return ResponseEntity.ok(new MsgResponse(syncResponse.getMessage(), syncResponse.getErr_code(), transaction_id));
        } catch (JSONException ex) {
            System.out.printf("Cấu trúc gói tin json hồ sơ không đúng định dạng quy định: %s!", ex.getMessage());
            return ResponseEntity.ok(new Response("Cấu trúc gói tin json M0001 hoặc M0001_1 không đúng định dạng quy định!", 1));
        } catch (Exception ex) {
            System.out.printf("Cập nhật dữ liệu CBCCVC không thành công: %s!", ex.getMessage());
            return ResponseEntity.ok(new Response("Cập nhật dữ liệu CBCCVC không thành công. " + ex.getMessage(), 1));
        }
    }

    //Tuyển dụng quá trình công tác
    @RequestMapping("/servicem0002")
    public ResponseEntity<?> ServiceM0002(@RequestHeader("Authorization") String token, @RequestBody Map<String, String> body) {
        try {
            JSONObject objtuyendungquatrinhcongtac;
            JSONArray arrquatrinhcongtac = null;

            MsgResponse msgresponse = ServiceValidate(token, body);
            if (msgresponse.getErr_code() == 1)
                return ResponseEntity.ok(msgresponse);

            JSONObject validate = new JSONObject(msgresponse.getTransaction_id());
            int hosonhansu_id = validate.getInt("Nhansu_Id");
            String sohieubndp_bnv = validate.getString("SoHieuCBCCVC_BNDP");
            String action_type = validate.getString("Action_Type");
            String transaction_id = validate.getString("Transaction_ID");
            JSONObject objhosocbccvc = validate.getJSONObject("HOSO_CBCCVC");

            //thông tin header
            JSONObject objheader = validate.getJSONObject("Header");
            objheader.put("Transaction_ID", transaction_id);

            //tuyển dụng quá trình công tác
            if (!objhosocbccvc.isNull("TUYENDUNG_QT_CONGTAC") && !objhosocbccvc.getJSONObject("TUYENDUNG_QT_CONGTAC").isEmpty()) {
                objtuyendungquatrinhcongtac = objhosocbccvc.getJSONObject("TUYENDUNG_QT_CONGTAC");
                if (!objtuyendungquatrinhcongtac.isNull("DS_QUATRINH_CONGTAC") && objtuyendungquatrinhcongtac.getJSONArray("DS_QUATRINH_CONGTAC").length() > 0)
                    arrquatrinhcongtac = objtuyendungquatrinhcongtac.getJSONArray("DS_QUATRINH_CONGTAC");
                else
                    arrquatrinhcongtac = null;
            } else objtuyendungquatrinhcongtac = null;

            //call stor cập nhật dữ liệu
            SyncResponse syncResponse = syncMsg.callStoreProcedureServicem0002(action_type, hosonhansu_id, objheader, objtuyendungquatrinhcongtac, arrquatrinhcongtac);

            System.out.println("---------------------import hosonhansu");
            System.out.println("Cập nhật thông tin về tuyển dụng quá trình công tác" + sohieubndp_bnv);
            return ResponseEntity.ok(new MsgResponse(syncResponse.getMessage(), syncResponse.getErr_code(), transaction_id));
        } catch (JSONException ex) {
            System.out.printf("Cấu trúc gói tin json không đúng định dạng quy định: %s!", ex.getMessage());
            return ResponseEntity.ok(new Response("Cấu trúc gói tin json M0002 không đúng định dạng quy định!", 1));
        } catch (Exception ex) {
            System.out.printf("Cập nhật thông tin về tuyển dụng, quá trình công tác không thành công: %s!", ex.getMessage());
            return ResponseEntity.ok(new Response("Cập nhật thông tin về tuyển dụng, quá trình công tác không thành công. " + ex.getMessage(), 1));
        }
    }

    //Lương phụ cấp chức vụ
    @RequestMapping("/servicem0003")
    public ResponseEntity<?> ServiceM0003(@RequestHeader("Authorization") String token, @RequestBody Map<String, String> body) {
        try {
            JSONObject objluongphucapchucvu;
            JSONArray arrquatrinhphucap = null, arrquatrinhluong = null;

            MsgResponse msgresponse = ServiceValidate(token, body);
            if (msgresponse.getErr_code() == 1)
                return ResponseEntity.ok(msgresponse);

            JSONObject validate = new JSONObject(msgresponse.getTransaction_id());
            int hosonhansu_id = validate.getInt("Nhansu_Id");
            String sohieubndp_bnv = validate.getString("SoHieuCBCCVC_BNDP");
            String action_type = validate.getString("Action_Type");
            String transaction_id = validate.getString("Transaction_ID");
            JSONObject objhosocbccvc = validate.getJSONObject("HOSO_CBCCVC");

            //thông tin header
            JSONObject objheader = validate.getJSONObject("Header");
            objheader.put("Transaction_ID", transaction_id);

            //lương phụ cấp chức vụ
            if (!objhosocbccvc.isNull("LUONG_PHUCAP_CHUCVU") && !objhosocbccvc.getJSONObject("LUONG_PHUCAP_CHUCVU").isEmpty()) {
                objluongphucapchucvu = objhosocbccvc.getJSONObject("LUONG_PHUCAP_CHUCVU");
                if (!objluongphucapchucvu.isNull("DS_QUATRINH_PHUCAP") && objluongphucapchucvu.getJSONArray("DS_QUATRINH_PHUCAP").length() > 0)
                    arrquatrinhphucap = objluongphucapchucvu.getJSONArray("DS_QUATRINH_PHUCAP");
                else arrquatrinhphucap = null;
                if (!objluongphucapchucvu.isNull("DS_QUATRINH_LUONG") && objluongphucapchucvu.getJSONArray("DS_QUATRINH_LUONG").length() > 0)
                    arrquatrinhluong = objluongphucapchucvu.getJSONArray("DS_QUATRINH_LUONG");
                else arrquatrinhluong = null;
            } else objluongphucapchucvu = null;

            //call stor cập nhật dữ liệu
            SyncResponse syncResponse = syncMsg.callStoreProcedureServicem0003(action_type, hosonhansu_id, objheader, objluongphucapchucvu, arrquatrinhphucap, arrquatrinhluong);

            System.out.println("---------------------import hosonhansu");
            System.out.println("Cập nhật thông tin về lương, phụ cấp, chức vụ" + sohieubndp_bnv);
            return ResponseEntity.ok(new MsgResponse(syncResponse.getMessage(), syncResponse.getErr_code(), transaction_id));
        } catch (JSONException ex) {
            System.out.printf("Cấu trúc gói tin json không đúng định dạng quy định: %s!", ex.getMessage());
            return ResponseEntity.ok(new Response("Cấu trúc gói tin json M0003 không đúng định dạng quy định!", 1));
        } catch (Exception ex) {
            System.out.printf("Cập nhật thông tin về lương, phụ cấp, chức vụ không thành công: %s!", ex.getMessage());
            return ResponseEntity.ok(new Response("Cập nhật thông tin về lương, phụ cấp, chức vụ không thành công. " + ex.getMessage(), 1));
        }
    }

    //Trinh độ đào tạo bồi dưỡng
    @RequestMapping("/servicem0004")
    public ResponseEntity<?> ServiceM0004(@RequestHeader("Authorization") String token, @RequestBody Map<String, String> body) {
        try {
            JSONObject objtrinhdodaotaoboiduong;
            JSONArray arrtinhoc = null, arrngoaingu = null, arrquatrinhdaotaoboiduong = null;

            MsgResponse msgresponse = ServiceValidate(token, body);
            if (msgresponse.getErr_code() == 1)
                return ResponseEntity.ok(msgresponse);

            JSONObject validate = new JSONObject(msgresponse.getTransaction_id());
            int hosonhansu_id = validate.getInt("Nhansu_Id");
            String sohieubndp_bnv = validate.getString("SoHieuCBCCVC_BNDP");
            String action_type = validate.getString("Action_Type");
            String transaction_id = validate.getString("Transaction_ID");
            JSONObject objhosocbccvc = validate.getJSONObject("HOSO_CBCCVC");

            //thông tin header
            JSONObject objheader = validate.getJSONObject("Header");
            objheader.put("Transaction_ID", transaction_id);

            //trinh độ đào tạo bồi dưỡng
            if (!objhosocbccvc.isNull("TRINHDO_DAOTAO_BOIDUONG") && !objhosocbccvc.getJSONObject("TRINHDO_DAOTAO_BOIDUONG").isEmpty()) {
                objtrinhdodaotaoboiduong = objhosocbccvc.getJSONObject("TRINHDO_DAOTAO_BOIDUONG");
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
            SyncResponse syncResponse = syncMsg.callStoreProcedureServicem0004(action_type, hosonhansu_id, objheader, objtrinhdodaotaoboiduong, arrtinhoc, arrngoaingu, arrquatrinhdaotaoboiduong);

            System.out.println("---------------------import hosonhansu");
            System.out.println("Cập nhật thông tin trình độ, đào tạo, bồi dưỡng " + sohieubndp_bnv);
            return ResponseEntity.ok(new MsgResponse(syncResponse.getMessage(), syncResponse.getErr_code(), transaction_id));
        } catch (JSONException ex) {
            System.out.printf("Cấu trúc gói tin json không đúng định dạng quy định: %s!", ex.getMessage());
            return ResponseEntity.ok(new Response("Cấu trúc gói tin json M0004 không đúng định dạng quy định!", 1));
        } catch (Exception ex) {
            System.out.printf("Cập nhật thông tin trình độ, đào tạo, bồi dưỡng không thành công: %s!", ex.getMessage());
            return ResponseEntity.ok(new Response("Cập nhật thông tin trình độ, đào tạo, bồi dưỡng không thành công. " + ex.getMessage(), 1));
        }
    }

    //Thông tin khác
    @RequestMapping("/servicem0005")
    public ResponseEntity<?> ServiceM0005(@RequestHeader("Authorization") String token, @RequestBody Map<String, String> body) {
        try {
            JSONObject objthongtinkhac;
            MsgResponse msgresponse = ServiceValidate(token, body);
            if (msgresponse.getErr_code() == 1)
                return ResponseEntity.ok(msgresponse);

            JSONObject validate = new JSONObject(msgresponse.getTransaction_id());
            int hosonhansu_id = validate.getInt("Nhansu_Id");
            String sohieubndp_bnv = validate.getString("SoHieuCBCCVC_BNDP");
            String action_type = validate.getString("Action_Type");
            String transaction_id = validate.getString("Transaction_ID");
            JSONObject objhosocbccvc = validate.getJSONObject("HOSO_CBCCVC");

            //thông tin header
            JSONObject objheader = validate.getJSONObject("Header");
            objheader.put("Transaction_ID", transaction_id);

            //thông tin khác
            if (!objhosocbccvc.isNull("THONGTIN_KHAC") && !objhosocbccvc.getJSONObject("THONGTIN_KHAC").isEmpty())
                objthongtinkhac = objhosocbccvc.getJSONObject("THONGTIN_KHAC");
            else objthongtinkhac = null;

            //call stor cập nhật dữ liệu
            SyncResponse syncResponse = syncMsg.callStoreProcedureServicem0005(action_type, hosonhansu_id, objheader, objthongtinkhac);

            System.out.println("---------------------import hosonhansu");
            System.out.println("Cập nhật thông tin khác " + sohieubndp_bnv);
            return ResponseEntity.ok(new MsgResponse(syncResponse.getMessage(), syncResponse.getErr_code(), transaction_id));
        } catch (JSONException ex) {
            System.out.printf("Cấu trúc gói tin json không đúng định dạng quy định: %s!", ex.getMessage());
            return ResponseEntity.ok(new Response("Cấu trúc gói tin json M0005 không đúng định dạng quy định!", 1));
        } catch (Exception ex) {
            System.out.printf("Cập nhật thông tin khác không thành công: %s!", ex.getMessage());
            return ResponseEntity.ok(new Response("Cập nhật thông tin khác không thành công. " + ex.getMessage(), 1));
        }
    }

    //Kết quả đánh giá xếp loại
    @RequestMapping("/servicem0006")
    public ResponseEntity<?> ServiceM0006(@RequestHeader("Authorization") String token, @RequestBody Map<String, String> body) {
        try {
            JSONArray arrketquadanhgia;
            MsgResponse msgresponse = ServiceValidate(token, body);
            if (msgresponse.getErr_code() == 1)
                return ResponseEntity.ok(msgresponse);

            JSONObject validate = new JSONObject(msgresponse.getTransaction_id());
            int hosonhansu_id = validate.getInt("Nhansu_Id");
            String sohieubndp_bnv = validate.getString("SoHieuCBCCVC_BNDP");
            String action_type = validate.getString("Action_Type");
            String transaction_id = validate.getString("Transaction_ID");
            JSONObject objhosocbccvc = validate.getJSONObject("HOSO_CBCCVC");

            //thông tin header
            JSONObject objheader = validate.getJSONObject("Header");
            objheader.put("Transaction_ID", transaction_id);

            //kết quả đánh giá xếp loại
            if (!objhosocbccvc.isNull("DS_KETQUA_DANHGIA_PHANLOAI") && objhosocbccvc.getJSONArray("DS_KETQUA_DANHGIA_PHANLOAI").length() > 0)
                arrketquadanhgia = objhosocbccvc.getJSONArray("DS_KETQUA_DANHGIA_PHANLOAI");
            else arrketquadanhgia = null;

            //call stor cập nhật dữ liệu
            SyncResponse syncResponse = syncMsg.callStoreProcedureServicem0006(action_type, hosonhansu_id, objheader, arrketquadanhgia);

            System.out.println("---------------------import hosonhansu");
            System.out.println("Cập nhật kết quả đánh giá, xếp loại " + sohieubndp_bnv);
            return ResponseEntity.ok(new MsgResponse(syncResponse.getMessage(), syncResponse.getErr_code(), transaction_id));
        } catch (JSONException ex) {
            System.out.printf("Cấu trúc gói tin json không đúng định dạng quy định: %s!", ex.getMessage());
            return ResponseEntity.ok(new Response("Cấu trúc gói tin json M0006 không đúng định dạng quy định!", 1));
        } catch (Exception ex) {
            System.out.printf("Cập nhật kết quả đánh giá, xếp loại không thành công: %s!", ex.getMessage());
            return ResponseEntity.ok(new Response("Cập nhật kết quả đánh giá, xếp loại không thành công. " + ex.getMessage(), 1));
        }
    }

    //lưu file json đơn vị gửi lên
    private boolean logTransaction(String json, String uniqueID) {
        boolean status = true;
        BufferedWriter bw = null;
        FileWriter fw = null;
        try {
            Date date = Calendar.getInstance().getTime();
            DateFormat dateFormatFile = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            String strDate = dateFormat.format(date);
            String data = String.format("%s  Transaction_ID: %s  Data: %s", strDate, uniqueID, json);
            File file = new File(String.format("logs/%s.log", dateFormatFile.format(date)));
            if (!new File("logs").exists())
                new File("logs").mkdir();
            if (!file.exists())
                file.createNewFile();
            fw = new FileWriter(file.getAbsoluteFile(), true);
            bw = new BufferedWriter(fw);
            bw.write(data);
            bw.newLine();
        } catch (IOException e) {
            status = false;
            e.printStackTrace();
        } finally {
            try {
                if (bw != null) bw.close();
                if (fw != null) fw.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return status;
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
    private int validateSohieuCBCCVC_BNDP(JSONObject jsonSohieucbccvc) {
        int status;
        SyncResponse syncResponse = syncMsg.callStoreProcedureServicemId("", jsonSohieucbccvc.toString());
        if (syncResponse.getErr_code() == 1)
            status = -1;
        else if (syncResponse.getValue().equals("0"))
            status = 0;
        else
            status = Integer.parseInt(syncResponse.getValue());
        return status;
    }
}

