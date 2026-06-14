package com.unirent.app.fragments;

import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import com.unirent.app.R;
import com.unirent.app.ai.ApiClient;
import com.unirent.app.activities.MainActivity;
import java.util.*;

public class AreaAdvisorFragment extends Fragment {
    private Spinner spSchool, spBudget;
    private RadioGroup rgVehicle, rgRoomType, rgPriority;
    private CardView cardResult;
    private TextView tvResult;
    private ProgressBar progress;
    private String resultArea = "";

    private static final String[] SCHOOLS = {
        "HUTECH - ĐH Công nghệ TP.HCM",
        "UTE - ĐH Sư phạm Kỹ thuật",
        "ĐH Bách Khoa",
        "ĐH Văn Lang",
        "ĐH Tôn Đức Thắng",
        "ĐH Kinh tế TP.HCM",
        "IUH - ĐH Công nghiệp",
        "ĐH KHXH&NV",
        "ĐH Sư phạm",
        "ĐH Khoa học Tự nhiên"
    };

    private static final String[] BUDGETS = {
        "Dưới 1.5 triệu",
        "1.5 - 2 triệu",
        "2 - 2.5 triệu",
        "2.5 - 3 triệu",
        "3 - 4 triệu",
        "Trên 4 triệu"
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inf, ViewGroup parent, Bundle s) {
        View root = inf.inflate(R.layout.fragment_area_advisor, parent, false);

        spSchool = root.findViewById(R.id.sp_school);
        spBudget = root.findViewById(R.id.sp_budget);
        rgVehicle = root.findViewById(R.id.rg_vehicle);
        rgRoomType = root.findViewById(R.id.rg_room_type);
        rgPriority = root.findViewById(R.id.rg_priority);
        cardResult = root.findViewById(R.id.card_result);
        tvResult = root.findViewById(R.id.tv_result);
        progress = root.findViewById(R.id.progress);

        spSchool.setAdapter(new ArrayAdapter<>(requireContext(),
            android.R.layout.simple_spinner_dropdown_item, SCHOOLS));
        spBudget.setAdapter(new ArrayAdapter<>(requireContext(),
            android.R.layout.simple_spinner_dropdown_item, BUDGETS));

        root.findViewById(R.id.btn_submit).setOnClickListener(v -> analyze());
        root.findViewById(R.id.btn_find).setOnClickListener(v -> goSearch());

        return root;
    }

    private void analyze() {
        String school = spSchool.getSelectedItem().toString();
        String budget = spBudget.getSelectedItem().toString();
        boolean hasBike = rgVehicle.getCheckedRadioButtonId() == R.id.rb_has_bike;
        boolean solo = rgRoomType.getCheckedRadioButtonId() == R.id.rb_solo;
        int priorityId = rgPriority.getCheckedRadioButtonId();
        String priority = priorityId == R.id.rb_near ? "gần trường" :
                          priorityId == R.id.rb_cheap ? "giá rẻ" : "gần chợ, siêu thị, trạm xe buýt";

        String prompt = String.format(
            "Tư vấn khu vực tìm trọ cho sinh viên:\n" +
            "- Trường: %s\n- Ngân sách: %s\n- %s\n- Muốn: %s\n- Ưu tiên: %s\n\n" +
            "Hãy gợi ý 2-3 khu vực/quận/phường cụ thể nên ở, kèm lý do ngắn. " +
            "Ví dụ: 'Với ngân sách 2.5-3tr, học UTE và có xe máy, bạn nên ưu tiên Linh Trung hoặc Tam Phú. " +
            "Nếu muốn gần trường hơn thì Linh Chiểu, Bình Thọ nhưng giá có thể cao hơn 1 chút.' " +
            "Trả lời bằng tiếng Việt, thân thiện, có emoji nhẹ.",
            school, budget, hasBike ? "Có xe máy" : "Không xe máy - đi xe buýt",
            solo ? "Ở 1 mình" : "Ở ghép", priority);

        progress.setVisibility(View.VISIBLE);
        cardResult.setVisibility(View.GONE);

        ApiClient.chat("Bạn là chuyên gia tư vấn nhà trọ cho sinh viên Việt Nam. Am hiểu các khu vực quanh trường đại học ở TP.HCM.",
            prompt, new ApiClient.Callback() {
                public void onSuccess(String r) {
                    progress.setVisibility(View.GONE);
                    resultArea = extractArea(r);
                    tvResult.setText(r);
                    cardResult.setVisibility(View.VISIBLE);
                }
                public void onError(String e) {
                    progress.setVisibility(View.GONE);
                    tvResult.setText("😅 AI đang bận, bạn thử lại sau nhé!\n\nGợi ý nhanh: Khu vực Thủ Đức, Quận 9 thường có giá tốt cho sinh viên.");
                    resultArea = "Thủ Đức";
                    cardResult.setVisibility(View.VISIBLE);
                }
            });
    }

    private String extractArea(String text) {
        // Tìm khu vực được mention nhiều nhất
        for (String area : new String[]{"Linh Trung", "Tam Phú", "Linh Chiểu", "Bình Thọ",
            "Tân Phú", "Tăng Nhơn Phú A", "Tăng Nhơn Phú B", "Phước Long A", "Phước Long B",
            "Thủ Đức", "Quận 9", "Bình Thạnh", "Gò Vấp", "Quận 10", "Quận 3", "Phú Nhuận"}) {
            if (text.contains(area)) return area;
        }
        return "Thủ Đức";
    }

    private void goSearch() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).switchToSearchWithArea(resultArea);
        }
    }
}
