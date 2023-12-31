package com.example.kakao.cart;

import com.example.kakao._core.security.SecurityConfig;
import com.example.kakao._core.utils.FakeStore;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.nullValue;


@Import({
        FakeStore.class,
        SecurityConfig.class
})
@WebMvcTest(controllers = {CartRestController.class})
public class CartRestControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    @DisplayName("장바구니 수정 테스트")
    @WithMockUser(username = "ssar@nate.com", roles = "USER")
    @Test
    public void update_test() throws Exception {
        // given
        List<CartRequest.UpdateDTO> requestDTOs = new ArrayList<>();
        CartRequest.UpdateDTO d1 = new CartRequest.UpdateDTO();
        d1.setCartId(1);
        d1.setQuantity(10);
        CartRequest.UpdateDTO d2 = new CartRequest.UpdateDTO();
        d2.setCartId(2);
        d2.setQuantity(10);
        requestDTOs.add(d1);
        requestDTOs.add(d2);
        String requestBody = om.writeValueAsString(requestDTOs);
        System.out.println("테스트 : "+requestBody);

        // when
        ResultActions result = mvc.perform(
                MockMvcRequestBuilders
                        .post("/carts/update")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
        );
        String responseBody = result.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : "+responseBody);

        // then
        result.andExpect(MockMvcResultMatchers.jsonPath("$.success").value("true"));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.response.carts[0].cartId").value(1));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.response.carts[0].optionId").value(1));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.response.carts[0].optionName").value("01. 슬라이딩 지퍼백 크리스마스에디션 4종"));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.response.carts[0].quantity").value(10));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.response.carts[0].price").value(100000));
    }

    @DisplayName("장바구니 담기 테스트")
    @WithMockUser(username = "ssar@nate.com", roles = "USER")
    @Test
    public void add_test() throws Exception {

        //given
        List<CartRequest.SaveDTO> requestDTOs = new ArrayList<>();
        CartRequest.SaveDTO saveDTO1 = new CartRequest.SaveDTO();
        saveDTO1.setOptionId(1);
        saveDTO1.setQuantity(5);

        CartRequest.SaveDTO saveDTO2 = new CartRequest.SaveDTO();
        saveDTO2.setOptionId(2);
        saveDTO2.setQuantity(5);

        requestDTOs.add(saveDTO1);
        requestDTOs.add(saveDTO2);

        String requestBody = om.writeValueAsString(requestDTOs);
        System.out.println("테스트 : "+requestBody);

        //when
        ResultActions result = mvc.perform(
                MockMvcRequestBuilders
                        .post("/carts/add")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        String responseBody = result.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : "+responseBody);

        //then
        result.andExpect(MockMvcResultMatchers.jsonPath("$.success").value("true"));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.response").value(nullValue()));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.error").value(nullValue()));
    }

    @DisplayName("장바구니 조회 테스트")
    @WithMockUser(username = "ssar@nate.com", roles = "USER")
    @Test
    public void findAll_test() throws Exception{


        //when
        ResultActions result = mvc.perform(
                MockMvcRequestBuilders
                        .get("/carts")
        );

        String responseBody = result.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : "+responseBody);

        //then
        result.andExpect(MockMvcResultMatchers.jsonPath("$.success").value("true"));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.response.products[0].carts[0].option.id").value(1));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.response.products[0].carts[0].quantity").value(10));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.response.products[0].carts[0].option.optionName").value("01. 슬라이딩 지퍼백 크리스마스에디션 4종"));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.response.products[0].carts[1].option.id").value(2));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.response.products[0].carts[1].quantity").value(10));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.response.products[0].carts[1].option.optionName").value("02. 슬라이딩 지퍼백 플라워에디션 5종"));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.response.totalPrice").value(209000));
    }

    @DisplayName("장바구니 비우기 테스트")
    @WithMockUser(username = "ssar@nate.com", roles = "USER")
    @Test
    public void clear_test() throws Exception {

        ResultActions result = mvc.perform(
                MockMvcRequestBuilders
                        .post("/carts/clear")
        );

        String responseBody = result.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : "+responseBody);

        //then
        result.andExpect(MockMvcResultMatchers.jsonPath("$.success").value("true"));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.response").value(nullValue()));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.error").value(nullValue()));
    }

    @DisplayName("인증없이 장바구니 조희 테스트")
    @Test
    public void findAll_Exception_test() throws Exception {

        ResultActions result = mvc.perform(
                MockMvcRequestBuilders
                        .get("/carts")
        );

        String responseBody = result.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        result.andExpect(MockMvcResultMatchers.jsonPath("$.success").value("false"));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.response").value(nullValue()));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.error.message").value("인증되지 않았습니다"));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.error.status").value(401));
    }

    @DisplayName("인증없이 장바구니 담기 테스트")
    @Test
    public void add_Exception_test() throws Exception {

        //given
        List<CartRequest.SaveDTO> requestDTOs = new ArrayList<>();
        CartRequest.SaveDTO saveDTO1 = new CartRequest.SaveDTO();
        saveDTO1.setOptionId(1);
        saveDTO1.setQuantity(5);

        CartRequest.SaveDTO saveDTO2 = new CartRequest.SaveDTO();
        saveDTO2.setOptionId(2);
        saveDTO2.setQuantity(5);

        requestDTOs.add(saveDTO1);
        requestDTOs.add(saveDTO2);

        String requestBody = om.writeValueAsString(requestDTOs);
        //System.out.println("테스트 : "+requestBody);

        //when
        ResultActions result = mvc.perform(
                MockMvcRequestBuilders
                        .post("/carts/add")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        String responseBody = result.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : "+responseBody);

        //then
        result.andExpect(MockMvcResultMatchers.jsonPath("$.success").value("false"));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.response").value(nullValue()));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.error.message").value("인증되지 않았습니다"));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.error.status").value(401));
    }

    @DisplayName("인증없이 장바구니 수정 테스트")
    @Test
    public void update_Exception_test() throws Exception {
        // given
        List<CartRequest.UpdateDTO> requestDTOs = new ArrayList<>();
        CartRequest.UpdateDTO d1 = new CartRequest.UpdateDTO();
        d1.setCartId(1);
        d1.setQuantity(10);
        CartRequest.UpdateDTO d2 = new CartRequest.UpdateDTO();
        d2.setCartId(2);
        d2.setQuantity(10);
        requestDTOs.add(d1);
        requestDTOs.add(d2);
        String requestBody = om.writeValueAsString(requestDTOs);
        //System.out.println("테스트 : "+requestBody);

        // when
        ResultActions result = mvc.perform(
                MockMvcRequestBuilders
                        .post("/carts/update")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
        );
        String responseBody = result.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : "+responseBody);

        // then
        result.andExpect(MockMvcResultMatchers.jsonPath("$.success").value("false"));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.response").value(nullValue()));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.error.message").value("인증되지 않았습니다"));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.error.status").value(401));
    }

}
