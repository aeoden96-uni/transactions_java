package transactions.main;


import transactions.backend.*;

public class Main {
    public static void main(String[] args) throws Exception {
        int N = 5;

        Linker linker = new Linker("linker_name",1,N);

        TwoPhaseCoord coord = new TwoPhaseCoord(linker);

        TwoPhaseParticipant[] participants = new TwoPhaseParticipant[N];
    }

}

//public class GlobalFuncTester implements FuncUser {
//    public i n t f u n c ( i n t x , i n t y ) {
//        r e t u r n + y ;
//        1
//        public s t a t i c void main( S t r i n g a r g s ) throws Exception {
//            i n t myId = I n t e g e r . p a r s e I n t ( a r g s [ 11);
//            i n t numProc = I n t e g e r . p a r s e I n t ( a r g s [ 2 ] ) ;
//            Linker corrmi = new Linker ( a r g s [ 01, myId, nuniProc);
//            GlobalFunc g = new GlobalFunc(comm, (myId == 0 ) ) ;
//            for ( i n t i = 0 ; i < numProc; i + + )
//            if ( i != myld)
//                i n t myvalue = I n t e g e r . p a r s e I n t ( a r g s [ : < I ) ;
//            GlobalFuncTester h = new GlobalFuncTester ( ) ;
//            g . i n i t i a l i z e (myvalue, h ) ;
//            i n t globalsum = g . computeGloba1 ( ) ;
//            System. o u t . p r i n t l n ("The global sum is " + globalSum);
//            (new ListenerThread ( i , g ) ) . s t a r t ( ) ;
//            1
//            t
