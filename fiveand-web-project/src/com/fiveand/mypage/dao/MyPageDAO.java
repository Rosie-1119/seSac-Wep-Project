package com.fiveand.mypage.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.fiveand.auction.board.vo.PagingVO;
import com.fiveand.auction.board.vo.ProductFileVO;
import com.fiveand.auction.board.vo.ProductVO;
import com.fiveand.member.vo.MemberVO;
import com.fiveand.util.ConnectionFactory;
import com.fiveand.util.JDBCClose;

public class MyPageDAO {

	/**
	 * 마이페이지(내정보 조회) 
	 */
	public MemberVO selectMyInfo(String id) {
		
		MemberVO member = null;	
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			conn = new ConnectionFactory().getConnection();
			StringBuilder sql = new StringBuilder();
			sql.append("select id, name, phone, email, warning_cnt ");
			sql.append(" from ftbl_member " );
			sql.append(" where id = ? " );
			

			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(1, id);
			
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				String name = rs.getString("name");
				String phone = rs.getString("phone");
				String email = rs.getString("email");
				int warningCnt = rs.getInt("warning_cnt");

				member = new MemberVO(id, name, phone, email, warningCnt);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCClose.close(pstmt, conn);
		}
		return member;

	}
	
	
	/**
	 * 마이페이지(내정보 수정) 
	 */
	public void updateMyInfo(MemberVO member) {
	
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try { 
			conn = new ConnectionFactory().getConnection();
			StringBuilder sql = new StringBuilder();
			sql.append(" update ftbl_member ");
			sql.append(" set pwd = ? , phone = ? , email = ? ");
			sql.append(" where id = ? ");

			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(1, member.getPwd());
			pstmt.setString(2, member.getPhone());
			pstmt.setString(3, member.getEmail());
			pstmt.setString(4, member.getId());
			
			pstmt.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCClose.close(pstmt, conn);
		}
	}
	
	

	
	/**
	 * 마이페이지(내 계정 삭제) 
	 */
	
	public void deleteMyInfo(MemberVO member) {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try { 
			conn = new ConnectionFactory().getConnection();
			StringBuilder sql = new StringBuilder();
			sql.append(" delete from ftbl_member ");
			sql.append(" where id = ? ");

			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(1, member.getId());

			pstmt.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCClose.close(pstmt, conn);
		}
	}
	
	
	
	/**
	 * ========페이징 처리 메소드=========
	 * 1. ftbl_product 의 전체 등록 제품수를 구하기
	 */
	public int totalProductCnt() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		int totalProduct = 0; //전체 글 수 
		
		try {
			conn = new ConnectionFactory().getConnection();
			StringBuilder sql = new StringBuilder();
			sql.append("select count(*) from (select * from ftbl_product order by pd_no desc) f ");
			pstmt = conn.prepareStatement(sql.toString());
			
			ResultSet rs = pstmt.executeQuery();
			rs.next();
			
			totalProduct = rs.getInt(1);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCClose.close(pstmt, conn);
		}
		return totalProduct;
	}
	

	
	
	/**
	 * 내가 등록한 auction목록
	 * 페이징 된 목록 출력
	 *  
	 */
	public List<ProductVO> pagingMyAuction(int currentPage, String id){
		
		PagingVO paging = new PagingVO();
		List<ProductVO> list = new ArrayList<ProductVO>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		int startNum = ((currentPage - 1) * paging.getDisplayRow()) + 1;
        int endNum = currentPage * paging.getDisplayRow();
        
        try {
			
        	conn = new ConnectionFactory().getConnection();
			StringBuilder sql = new StringBuilder();
			sql.append("select * from (select rownum as row_num, board.* from ( select p.pd_no, p.pd_name, p.start_price, p.reg_date, to_char(p.due_date, 'mm-dd') as due_date , p.c_no, c.category, f.file_save_name ");
			sql.append(" from ftbl_product p, ( select pd_no,  row_number() over(partition by pd_no order by pd_no) row_num, file_save_name from  ftbl_product_file) f, ftbl_category c ");
			sql.append(" where row_num = 1 and p.pd_no = f.pd_no and p.c_no = c.c_no and id = ? ");
			sql.append(" order by pd_no desc) board) ");
			sql.append(" where row_num >= ? and row_num <= ? ");
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(1, id);
			pstmt.setInt(2, startNum);
			pstmt.setInt(3, endNum);
		
			ResultSet rs = pstmt.executeQuery();
			
			while(rs.next()) {
				ProductVO productVO = new ProductVO();
				
				productVO.setPdNo(rs.getInt("pd_no"));
				productVO.setPdName(rs.getString("pd_name"));
				productVO.setStartPrice(rs.getInt("start_price"));
				productVO.setDueDate(rs.getString("due_date"));
				productVO.setcNo(rs.getInt("c_no"));
				productVO.setcName(rs.getString("category"));
				productVO.setFileSaveName(rs.getString("file_save_name"));
				
				list.add(productVO);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCClose.close(pstmt, conn);
		}
        
		return list;
	}
	
	
	
	
	/**
	 * 내가 등록한 하트 목록 -------미완.
	 *  
	 */
	
	public List<MemberVO> selectMyHeart() {
		
		List<MemberVO> list = new ArrayList<>();	
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			conn = new ConnectionFactory().getConnection();
			StringBuilder sql = new StringBuilder();
			sql.append("pd_no, pd_name, , email, warning_cnt ");
			sql.append(" from ftbl_product");

			pstmt = conn.prepareStatement(sql.toString());
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				String id = rs.getString("id");
				String name = rs.getString("name");
				String phone = rs.getString("phone");
				String email = rs.getString("email");
				int warningCnt = rs.getInt("warning_cnt");

				MemberVO member = new MemberVO(id, name, phone, email, warningCnt);
				list.add(member);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCClose.close(pstmt, conn);
		}
		return list;

	}
	
	
	
	
	
	
	
	
	
	
	
	
	
}
