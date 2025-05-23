package com.app.mypage.controller;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.app.Action;
import com.app.Result;
import com.app.dao.MemberDAO;
import com.app.vo.MemberVO;

public class MypageInfoController implements Action {
    @Override
    public Result execute(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        
        HttpSession session = req.getSession();
        System.out.println("▶ [MypageInfoController] 요청 도착");
        System.out.println("▶ mode = " + req.getParameter("mode"));
        System.out.println("▶ session loginId = " + session.getAttribute("loginId"));
        Long id = (Long) session.getAttribute("loginId");

		/*
		 * // 임시 이메일 설정 (개발용) if (email == null) { email = "wlsud94@naver.com";
		 * session.setAttribute("userEmail", email); }
		 */
        // DAO 준비
        MemberDAO memberDAO = new MemberDAO();
        Optional<MemberVO> memberVO = memberDAO.selectById(id);

        // 요청 구분: check or change
        String mode = req.getParameter("mode");
       
        if (mode == null) {
            Result result = new Result();
            if(memberVO.isPresent()) {
            	req.setAttribute("member", memberVO.get());
            }
            
            result.setPath("mypage-Infokkk.jsp");
            result.setRedirect(false);
            return result;
        }
        
        //  비밀번호 확인
        if ("check".equals(mode)) {
        	
            String inputPassword = req.getParameter("password");

            if (memberVO.isPresent() && memberVO.get().getMemberPassword().equals(inputPassword)) {
                resp.getWriter().write("{\"result\":\"success\"}");
            } else {
                resp.getWriter().write("{\"result\":\"fail\"}");
            }
        }

        //  비밀번호 변경
        if ("change".equals(mode)) {
        	
            String newPassword = req.getParameter("newPassword");
            System.out.println("▶ newPassword = " + newPassword);
            System.out.println("▶ memberVO.isPresent() = " + memberVO.isPresent());
            if (memberVO.isPresent()) {
                MemberVO member = memberVO.get();
                member.setMemberPassword(newPassword);
                System.out.println("▶ 비밀번호 업데이트 시도");
                memberDAO.updatePassword(member);

                resp.getWriter().write("{\"result\":\"success\"}");
            } else {
                resp.getWriter().write("{\"result\":\"fail\"}");
            }
            return null;
        }

        return null;
    }
}