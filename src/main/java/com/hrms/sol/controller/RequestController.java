package com.hrms.sol.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hrms.sol.entity.Request;
import com.hrms.sol.repository.RequestRepository;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RequestMapping("/api")
@RestController
@CrossOrigin
public class RequestController {

    @Autowired
    private RequestRepository repo;

    @PostMapping("/admin/req")
    public Request createRequest(@RequestBody Map<String, String> reqBody) {

        Request req = new Request();

        req.setStatus("PENDING");
        req.setReason(reqBody.get("reason"));
        req.setType(reqBody.get("type"));
        req.setPayload(reqBody.get("payload"));
        req.setUserId(Long.parseLong(reqBody.get("userId")));
        return repo.save(req);
    }

    @GetMapping("/user/req/{id}")
    public List<Request> getByUser(@PathVariable Long id) {
            return repo.findByUserId(id);
    }

    @GetMapping("/admin/req")
    public List<Map<String, Object>> getByStatus(@RequestParam String status) {

        List<Object[]> rows = repo.findByStatus(status);

        List<Map<String, Object>> result = new ArrayList<>();

        for (Object[] row : rows) {
            Request req = (Request) row[0];
            String name = (String) row[1];

            Map<String, Object> map = new HashMap<>();
            map.put("id", req.getId());
            map.put("type", req.getType());
            map.put("status", req.getStatus());
            map.put("payload", req.getPayload());
            map.put("reason", req.getReason());
            map.put("userId", req.getUserId());
            map.put("name", name);

            result.add(map);
        }

        return result;
    }

    @PutMapping("/admin/req/{id}")
    public Request updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
                
        Request req = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        req.setStatus(body.get("status")); 
        req.setRemark(body.get("remark")); 

        return repo.save(req);
    }

}
