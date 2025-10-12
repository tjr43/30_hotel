package Ex_mmhotel;
//퀴즈에 대한 정보 담기
public class Trap {
    private String description; //함정의 종류나 특징
    private String riddle;      //수수께끼 내용 저장
    private String answer;      //수수께끼 정답 저장

    public Trap(String description, String riddle, String answer){
        this.description = description;
        this.riddle = riddle;
        this.answer = answer;
    }
    public String getDescription(){return description;}
    public String getRiddle(){return riddle;}
    public String getAnswer(){return answer;}

}
