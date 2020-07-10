package dduwcom.mobile.proto;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;

import androidx.appcompat.app.AppCompatActivity;

public class result extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);

        TextView myTitle = findViewById(R.id.washTitle1);
      //  TextView result = findViewById(R.id.result);

        Intent intent = getIntent();
        int[] myResult = intent.getIntArrayExtra("result");

       // String monthResult = "청소 " + Integer.toString(myResult[0])+ "번\n" + "요리 " + Integer.toString(myResult[1]) + "번\n" + "빨래 " + Integer.toString(myResult[2]) + "번\n"+ "외식 " + Integer.toString(myResult[3]) + "번\n";

        HashMap<Integer,String> map = new HashMap<Integer,String >();//key,value값 저장해주기 위해 map 사용
        map.put(myResult[0],"청소");
        map.put(myResult[1],"분리수거");
        map.put(myResult[2],"빨래");
        map.put(myResult[3],"기타");


        TreeMap sortedHashMap = new TreeMap(map);//map 정렬 오름차순
        String value = "";
        Iterator<Integer> Iter = sortedHashMap.keySet().iterator();
        while(Iter.hasNext()){
            int key = Iter.next();
            value = sortedHashMap.get(key).toString();//맨 마지막 값 value로 저장 넘 비효율적ㅠ
        }

        /*
        Arrays.sort(myResult);
        String highest = Integer.toString(myResult[myResult.length-1]);
        */


        int experiencePeriod = 2;//일단 2년으로 설정 나중에 입력받으면 갖다쓸거임
        int totalProgressValue = 20;//20으로 걍 설정
        int cleanPercent = 10;
        int washPercent = 70;
        int recyclingPercent = 50;
        int etcPercent = 20;

        String title = value + "의 달인";//이름은 추후 입력받은 값 저장한 후 사용하기

        String experience_capability = getString(R.string.experience_capability);
        String clean_capability = getString(R.string.clean_capability);
        String wash_capability = getString(R.string.wash_capability);
        String recycling_capability = getString(R.string.recycling_capability);
        String etc_capability = getString(R.string.etc_capability);

        String experienceResultText = String.format(experience_capability,experiencePeriod,totalProgressValue);
        String cleanResultText = String.format(clean_capability,cleanPercent);
        String washResultText = String.format(wash_capability,washPercent);
        String recyclingText = String.format(recycling_capability,recyclingPercent);
        String etcText = String.format(etc_capability,etcPercent);

        //예외처리 해줘야한당

        ProgressBar totalProgress = (ProgressBar)findViewById(R.id.totalProgressbar);
        ProgressBar cleanProgrss = (ProgressBar)findViewById(R.id.cleanProgressBar);
        ProgressBar washProgress = (ProgressBar)findViewById(R.id.washProgressBar);
        ProgressBar recyclingProgress = (ProgressBar)findViewById(R.id.recyclingProgressBar);
        ProgressBar etcProgress = (ProgressBar)findViewById(R.id.etcProgressBar);

        totalProgress.setProgress(totalProgressValue);
        cleanProgrss.setProgress(cleanPercent);
        washProgress.setProgress(washPercent);
        recyclingProgress.setProgress(recyclingPercent);
        etcProgress.setProgress(etcPercent);

        TextView periodCapability = findViewById(R.id.periodCapability);
        TextView cleanCapability = findViewById(R.id.cleaning);
        TextView washCapability = findViewById(R.id.wash);
        TextView recyclingCapability = findViewById(R.id.recycling);
        TextView etcCapability = findViewById(R.id.etc);

        periodCapability.setText(experienceResultText);//혼자살기 경력 %d년차 | 자취 능력 %d\%
        cleanCapability.setText(cleanResultText);
        washCapability.setText(washResultText);
        recyclingCapability.setText(recyclingText);
        etcCapability.setText(etcText);

        myTitle.setText(title);
        //result.setText(monthResult);
    }
}
