package com.maru.chaekmaru.mypage;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.maru.chaekmaru.book.BookDto;
import com.maru.chaekmaru.member.MemberDto;
import com.maru.chaekmaru.review.ReviewDto;
import com.maru.chaekmaru.review.ReviewService;
import com.maru.chaekmaru.shop.SaledBookDto;

import jakarta.servlet.http.HttpSession;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Controller
@RequestMapping("/mypage")
public class MypageController {

	@Autowired
	MypageService mypageService;

	@Autowired
	ReviewService reviewService;

	/*
	 * 장바구니 목록 페이지 이동
	 */
	@GetMapping("/member_cart_form")
	public String myCartList(Model model, HttpSession session) {
		log.info("myCartList");

		String nextPage = "mypage/member_cart_form";

		MemberDto loginedMemberDto = (MemberDto) session.getAttribute("loginedMemberDto");

		List<MemberCartDto> memberCartDtos = mypageService.getMyCartList(loginedMemberDto.getM_id());

		model.addAttribute("memberCartDtos", memberCartDtos);

		return nextPage;

	}

	/*
	 * 장바구니에서 수량 변경 클릭
	 */
	@GetMapping("/cart_modify_form")
	public String cartmodifyform(HttpSession session, Model model, @RequestParam("c_no") int c_no,
			@RequestParam("c_book_count") int c_book_count) {
		log.info("cartmodifyform");

		String nextPage = "redirect:/mypage/member_cart_form";

		MemberDto loginedMemberDto = (MemberDto) session.getAttribute("loginedMemberDto");

		int result = mypageService.addBookCount(loginedMemberDto.getM_id(), c_no, c_book_count);
		if (result < 0) {
			nextPage = "/";
		}

		return nextPage;

	}

	/*
	 * 도서 상세 페이지 장바구니 클릭
	 */
	@PostMapping("/member_cart_form")
	public String addMyCart(HttpSession session, Model model, @RequestParam("b_no") int b_no) {
		log.info("addMyCart");

		String nextPage = "mypage/member_cart_form";

		MemberDto loginedMemberDto = (MemberDto) session.getAttribute("loginedMemberDto");

		int result = mypageService.addMyCart(loginedMemberDto.getM_id(), b_no);

		return nextPage;

	}

	/*
	 * 마이 페이지 장바구니 삭제 클릭
	 */
	@GetMapping("/delete_mycart_confirm")
	public String deleteMyCart(HttpSession session, @RequestParam("c_no") int c_no) {
		log.info("deleteMyCartList");

		String nextPage = "redirect:/mypage/member_cart_form";

		MemberDto loginedMemberDto = (MemberDto) session.getAttribute("loginedMemberDto");

		int result = mypageService.deleteMyCart(loginedMemberDto.getM_id(), c_no);

		if (result <= 0) {
			log.info("Delete Fail");
//			nextPage = "/fail";
		}

		return nextPage;

	}

	/*
	 * 결제 폼으로 이동
	 */
	@GetMapping("/payment_form")
	public String paymentMyCartForm(Model model, HttpSession session, @RequestParam("c_no") int c_no) {
		log.info("paymentMyCartListForm");

		String nextPage = "mypage/payment_form";

		MemberDto loginedMemberDto = (MemberDto) session.getAttribute("loginedMemberDto");

		List<MemberCartDto> memberCartDtos = mypageService.paymentForm(loginedMemberDto.getM_id(), c_no);
		
		int allPrice = mypageService.calcAllPrice(memberCartDtos);
		int discount = mypageService.memberDiscount(allPrice, loginedMemberDto.getM_grade());

		model.addAttribute("memberCartDtos", memberCartDtos);
		model.addAttribute("allPrice", allPrice);
		model.addAttribute("discount", discount);
		model.addAttribute("finalPrice", allPrice - discount + 3000);

		return nextPage;

	}

	/*
	 * 결제 폼에서 결제하기 버튼 클릭
	 */
	@PostMapping("/payment_form_confirm")
	public String paymentMyCartList(HttpSession session, @ModelAttribute SaledBookDto saledBookDto) {
		log.info("======================paymentMyCartList===================");

		MyPointListDto myPointListDto = new MyPointListDto();

		String nextPage = "mypage/payment_form_confirm";

		MemberDto loginedMemberDto = (MemberDto) session.getAttribute("loginedMemberDto");

		int result = mypageService.paymentMyCart(saledBookDto, loginedMemberDto.getM_id());
		if (result > 0) {
			myPointListDto.setM_id(loginedMemberDto.getM_id());
			myPointListDto.setPl_payment_book_point(saledBookDto.getSb_all_price());
			myPointListDto.setPl_desc("도서 " + saledBookDto.getSb_book_count() + "권 구매");

			result = mypageService.insertPoint(myPointListDto);
			if (result > 0) {
				result = mypageService.deletePaymentMyCart(loginedMemberDto.getM_id());

			}

		}

		return nextPage;

	}

	/*
	 * 리뷰 페이지로 이동
	 */
	@GetMapping("/my_review")
	public String myReview(Model model, HttpSession session) {
		String nextPage = "/mypage/my_review";

		MemberDto loginedMemberDto = (MemberDto) session.getAttribute("loginedMemberDto");

		log.info(loginedMemberDto.getM_id());
		ArrayList<ReviewDto> myReviews = reviewService.setMyReview(loginedMemberDto.getM_id());
		model.addAttribute("reviews", myReviews);

		return nextPage;
		
	}

	/*
	 * 장바구니 모두 결제 버튼 클릭
	 */
	@GetMapping("/all_payment_form")
	public String allPaymentMyCartForm(Model model, HttpSession session) {
		log.info("allPaymentMyCartForm");

		String nextPage = "mypage/all_payment_form";

		MemberDto loginedMemberDto = (MemberDto) session.getAttribute("loginedMemberDto");

		List<MemberCartDto> memberCartDtos = mypageService.allPaymentForm(loginedMemberDto.getM_id());
		int allPrice = mypageService.calcAllPrice(memberCartDtos);
		int discount = mypageService.memberDiscount(allPrice, loginedMemberDto.getM_grade());

		model.addAttribute("memberCartDtos", memberCartDtos);
		model.addAttribute("allPrice", allPrice);
		model.addAttribute("discount", discount);
		model.addAttribute("finalPrice", allPrice - discount + 3000);

		return nextPage;
		
	}
	
	/*
	 * 도서 상세 페이지에서 장바구니 버튼 클릭
	 */
	@GetMapping("/add_cart")
	public String moveMyCart(HttpSession session, @RequestParam("b_no") int b_no) {

		String nextPage = "redirect:/mypage/member_cart_form";

		MemberDto loginedMemberDto = (MemberDto) session.getAttribute("loginedMemberDto");

		int result = mypageService.addMyCart(loginedMemberDto.getM_id(), b_no);
	
		return nextPage;

	}
	
	/*
	 * 모두 결제 폼에서 결제하기 버튼 클릭
	 */
	@PostMapping("/all_payment_form_confirm")
	public String allPaymentMyCartList(HttpSession session, @ModelAttribute SaledBookDto saledBookDto) {
		log.info("<=====================allPaymentMyCartList==================>");

		MyPointListDto myPointListDto = new MyPointListDto();

		String nextPage = "mypage/payment_form_confirm";

		MemberDto loginedMemberDto = (MemberDto) session.getAttribute("loginedMemberDto");

		int result = mypageService.allPaymentMyCartList(loginedMemberDto.getM_id(), saledBookDto);
		if (result > 0) {
			myPointListDto.setM_id(loginedMemberDto.getM_id());
			myPointListDto.setPl_payment_book_point(saledBookDto.getSb_all_price());

			result = mypageService.insertAllPoint(myPointListDto, loginedMemberDto.getM_id());
			if (result > 0) {
				result = mypageService.deleteAllMyCart(loginedMemberDto.getM_id());

			}

		}

		return nextPage;

	}
	
	/*
	 * 도서 상세 페이지에서 바로 결제 버튼 클릭
	 */
	@GetMapping("/move_payment")
	public String movePayment(HttpSession session, @RequestParam("b_no") int b_no) {

		String nextPage = "redirect:/mypage/payment_form";

		MemberDto loginedMemberDto = (MemberDto) session.getAttribute("loginedMemberDto");

		MemberDto memberDto = new MemberDto();
		memberDto.setM_addr(loginedMemberDto.getM_addr());
		memberDto.setM_addr_code(loginedMemberDto.getM_addr_code());
		memberDto.setM_detail_addr(loginedMemberDto.getM_detail_addr());
		memberDto.setM_name(loginedMemberDto.getM_name());
		memberDto.setM_grade(loginedMemberDto.getM_grade());
		
		int result = mypageService.movePayment(loginedMemberDto.getM_id(), b_no, memberDto);
	
		return nextPage;

	}
	

}
