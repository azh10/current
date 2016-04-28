import java.util.ArrayList;
import java.util.HashMap;

public class MiniMax 
{
	int m_nMaxPly = 7;

	//  This is the piece to search for. Should be either RED or YELLOW.
	int m_nSearchPiece = 0;
	
	// memoize
	private static HashMap<String, Integer> memoboard;
	private static ArrayList<Integer> memoHashList;

	// Set the piece for which a move will be found.
	public void SetSearchPiece( int nPiece )
	{
		m_nSearchPiece = nPiece;
	}

	// Compile a list of legal moves.
	int GetLegalMoves( int[][] nBoardData, int[] nMoveList )
	{
		int row, col;
		int nNumMoves = 0;

		// Loop through six rows.
		for( row=0; row<=5; row++ )
		{
			// Loop through seven columns.
			for( col=0; col<=6; col++ )
			{
				// If the board at this location is empty,
				//   then this square is a legal move.
				if( nBoardData[row][col] == Connect4.EMPTY &&
						( row == 5 || nBoardData[row+1][col] != Connect4.EMPTY ) )
				{
					// Put the row and column in the move list array.
					nMoveList[nNumMoves*2] = row;
					nMoveList[nNumMoves*2+1] = col;
					// Increment the index pointer.
					nNumMoves++;
				}
			}
		}

		// Return the number of legal moves that were found.
		return( nNumMoves );
	}
/*
 * 
Please note: there is no memoization in this pseudocode
Position pos is a data structure with Row and Col 
int nPiece is 0 for red and 1 for yellow 
int nDepth should start at 0 Board brd is a class containing the board 
	and board methods 
int nAlpha starts at negative infinity 
int nBeta starts at positive infinity
To get the next piece to place, make the following 
	call: DoSearche pos, nPiece, nDepth, brd, alpha, beta
-------------------------------------------------------------------
DoSearche pos, nPiece, nDepth, brd, nAlpha, nBeta   
// If side won then we return either high or low values.
  if brd.DidSideWin() return 2000000 for maximizer and -2000000 for minimizer
  // This is neutral for both sides.   
	if brd.IsCatsGame() return 0
  
  // We will stop once we get to the designated depth and   
  //   simply return the score.   
  if we are at a leaf depth return the score

  
  // Here we get the list of legal moves for the current board. 
  MoveCount = GetLegalMoves brd.GetBoardData(), MoveList
  
  // Safety so that we always have a move at depth 0.   
  if nDepth == 0 then set pos.Row = MoveList[0] and pos.Col = MoveList[1]
  
  // Check to see if this is the maximizer. You could also use   
  //   the depth % 2 == 0 to test for maximizer.   
  if nPiece == nSearchPiece then
    // There are two cases: maximizer and minimzer  
		for the maximizer
      // Go through the move list     
			foreach move in move list
        // We will use the cloned board on which to make moves and        
				//   make the recursive call.       
				clone the board         
				// Make the move         
				make the move on the cloned board (call placepiece)
        // We need to save a copy of the alpha for later.         
        savealpha = nAlpha
        // Make the recursive call and record the returned value        
				retvalue = DoSearche pos, nPiece ^ 1, nDepth + 1, clonedboard, nAlpha, nBeta
        // The new alpha will be the maximum of alpha and the return value.        
				//   We have to remember the maximum since this is the maximizer.        
				nAlpha = max nAlpha, retvalue
        // If this move beats the previously recorded move, record this one in the data structure.
        if nDepth == 0 and savealpha < nAlpha then          
					set pos.Row = MoveList[0] and pos.Col = MoveList[1]
        if retvalue >= nBeta then          
					return retvalue
      // PLEASE note the level of logic for this line      
			return alpha
    // The minimizer is slightly different than that maximizer    
    else for the minimizer
      // Go through the move list      
			foreach move in move list
        // We will use the cloned board on which to make moves and        
				//   make the recursive call.        
				clone the board         
				// Make the move        
				make the move on the cloned board (call placepiece)
        // Make the recursive call and record the returned value        
				retvalue = DoSearche pos, nPiece ^ 1, nDepth + 1, clonedboard, nAlpha, nBeta
        // The new beta will be the mainimum of beta and the return value.         
        //   We have to remember the minimum since this is the minimizer.        
				nBeta = min nBeta retvalue
        if retvalue <= beta then          
        	return retvalue
    // PLEASE note the level of logic for this line      
		return beta
/*
 */
	int DoSearche( Position pos, int nPiece, int nDepth, Board pBoard, int alph, int beta )
	{
		// Local arrays for the legal move list and
		//   the result list.
		int[] nMoveList = new int[7*2];

		// Create min and max variables.
		int nMin = 2000000, nMax = -2000000;

		// First, see if a side has won.
		if( pBoard.DidSideWin( nPiece ) )
		{
			if( nPiece == m_nSearchPiece )
			{
				return( 2000000 );
			}
			return( -2000000 );
		}

		// See if we have a Cats game.
		else if( pBoard.IsCatsGame() )
		{
			// Score for Cats game is 0.
			return( 0 );
		}

		// If we are at a leaf node, return the score.
		else if( nDepth >= m_nMaxPly )
		{
			return( ScoreIt( m_nSearchPiece, pBoard.GetBoardData() ) );
		}

		// Get the legal moves.
		int nMoves = GetLegalMoves( pBoard.GetBoardData(), nMoveList );
		
		if( nPiece == m_nSearchPiece ){
			// Loop through the legal moves.
			for( int i=0; i<nMoves; i++ )
			{
				// We need a board clone so that we can place pieces
				//   without messing up previous board positions.
				Board SaveMe = pBoard.Clone();
				
				// Place the piece from the current move in the list.
				SaveMe.PlacePiece( nMoveList[i*2], nMoveList[i*2+1], nPiece );
				
				int savedAlph = alph;
				// Call DoSearche() recursively.
				int res = DoSearche( pos, nPiece ^ 1, nDepth + 1, SaveMe, alph, beta );
				alph = Math.max( alph, res );
				
				if( nDepth == 0 && savedAlph < alph ){
					pos.Row = nMoveList[i*2];
					pos.Col = nMoveList[i*2+1];
				}
				if( res >= beta )
					return res;
			}
			
			return alph;
		}else{
			// Loop through the legal moves.
			for( int i=0; i<nMoves; i++ )
			{
				// We need a board clone so that we can place pieces
				//   without messing up previous board positions.
				Board SaveMe = pBoard.Clone();
				
				// Place the piece from the current move in the list.
				SaveMe.PlacePiece( nMoveList[i*2], nMoveList[i*2+1], nPiece );
				
				int savedBeta = beta;
				// Call DoSearchee() recursively.
				int res = DoSearche( pos, nPiece ^ 1, nDepth + 1, SaveMe, alph, beta );
				beta = Math.min( beta, res );
				
				if( nDepth == 0 && savedBeta > beta )
				{
					pos.Row = nMoveList[0];
					pos.Col = nMoveList[1]; 
				}
				if( res <= alph )
					return res;
			}
			return beta;
		}
	}

	// Wrapper method that kicks off minimax to get a move.
	public void GetMove( Position pos, int[][] BoardData, int nPiece )
	{
		// Set the search piece.
		SetSearchPiece( nPiece );

		// Create a new board with this board data.
		Board brd = new Board();
		brd.SetBoardData( BoardData );

		// Call the recursive method.
		DoSearche( pos, nPiece, 0, brd, Integer.MIN_VALUE, Integer.MAX_VALUE );
	}

	int DoSearch( Position pos, int nPiece, int nDepth, Board pBoard, int alpha, int beta )
	{
		// Local arrays for the legal move list and
		//   the result list.
		int[] nMoveList = new int[7*2];
		
		// Create min and max variables.
		int nMin = 2000000, nMax = -2000000;

		// First, see if a side has won.
		if( pBoard.DidSideWin( nPiece ) ){
			if( nPiece == m_nSearchPiece ){
				return( 2000000 );
			}
			return( -2000000 );
		}
		
		// See if we have a Cats game.
		else if( pBoard.IsCatsGame() ){
			// Score for Cats game is 0.
			return( 0 );
		}
		
		// If we are at a leaf node, return the score.
		else if( nDepth >= m_nMaxPly ){
			return( ScoreIt( m_nSearchPiece, pBoard.GetBoardData() ) );
		}

		// Get the legal moves.
		int nMoves = GetLegalMoves( pBoard.GetBoardData(), nMoveList );
		
		if( nDepth == 0 ){
			pos.Row = nMoveList[0];
			pos.Col = nMoveList[1]; 
		}
		
		if( nPiece == m_nSearchPiece ){
			// Loop through the legal moves.
			for( int i=0; i<nMoves; i++ ){
				// We need a board clone so that we can place pieces
				//   without messing up previous board positions.
				Board SaveMe = pBoard.Clone();
				
				// Place the piece from the current move in the list.
				SaveMe.PlacePiece( nMoveList[i*2], nMoveList[i*2+1], nPiece );
				
				// Call DoSearch() recursively.
				int res = DoSearch( pos, nPiece ^ 1, nDepth + 1, SaveMe, alpha, beta );
	
				// Check to see if this result is less than the current min.
				if( nMin > res ){
					// Set nMin.
					nMin = res;
				}
				
				// Check to see if this result is greater than the current max.
				if( nMax < res ){
					// Set nMax.
					nMax = res;
					
					if( nDepth == 0 ){
						pos.Row = nMoveList[i*2];
						pos.Col = nMoveList[i*2+1]; 
					}
				}
			}
			
			// If this is max of minimax, then return the max.
			if( nPiece == m_nSearchPiece ){
				return( nMax );
			}
			
			// If this is min of minimax, then return the min.
			else{
				return( nMin );
			}
		}else{
			
			// Loop through the legal moves.
			for( int i=0; i<nMoves; i++ ){
				// We need a board clone so that we can place pieces
				//   without messing up previous board positions.
				Board SaveMe = pBoard.Clone();
				
				// Place the piece from the current move in the list.
				SaveMe.PlacePiece( nMoveList[i*2], nMoveList[i*2+1], nPiece );
				
				// Call DoSearch() recursively.
				int res = DoSearch( pos, nPiece ^ 1, nDepth + 1, SaveMe, alpha, beta );
	
				// Check to see if this result is less than the current min.
				if( nMin > res ){
					// Set nMin.
					nMin = res;
				}
				
				// Check to see if this result is greater than the current max.
				if( nMax < res ){
					// Set nMax.
					nMax = res;
					
					if( nDepth == 0 ){
						pos.Row = nMoveList[i*2];
						pos.Col = nMoveList[i*2+1]; 
					}
				}
			}
			
			// If this is max of minimax, then return the max.
			if( nPiece == m_nSearchPiece ){
				return( nMax );
			}
			
			// If this is min of minimax, then return the min.
			else{
				return( nMin );
			}
		}
	}

	int ScoreIt( int nPiece, int[][] BoardData )
	{
		int Twos = 0;
		int Threes = 0;
		int Fours =0 ;

		for( int nRow=0; nRow<6; nRow++ )
		{
			int nCount = 0;
			for( int nCol=0; nCol<7; nCol++ )
			{
				if( BoardData[nRow][nCol] == nPiece )
				{
					nCount++;
				}
				else
				{
					if( nCount == 2 )
					{
						Twos++;
					}
					else if( nCount == 3 )
					{
						Threes++;
					}
					else if( nCount == 4 )
					{
						Fours++;
					}
					nCount = 0;
				}
			}
			if( nCount == 2 )
			{
				Twos++;
			}
			else if( nCount == 3 )
			{
				Threes++;
			}
			else if( nCount == 4 )
			{
				Fours++;
			}
		}

		for( int nCol=0; nCol<7; nCol++ )
		{
			int nCount = 0;
			for( int nRow=0; nRow<6; nRow++ )
			{
				if( BoardData[nRow][nCol] == nPiece )
				{
					nCount++;
				}
				else
				{
					if( nCount == 2 )
					{
						Twos++;
					}
					else if( nCount == 3 )
					{
						Threes++;
					}
					else if( nCount == 4 )
					{
						Fours++;
					}
					nCount = 0;
				}
			}
			if( nCount == 2 )
			{
				Twos++;
			}
			else if( nCount == 3 )
			{
				Threes++;
			}
			else if( nCount == 4 )
			{
				Fours++;
			}
		}

		// Loop through the diagonal data.
		for( int nDiagonalTest=0; nDiagonalTest<Board.m_nDiagonalData.length/4; nDiagonalTest++)
		{
			int nCount = 0;
			// Starting row.
			int nRow = Board.m_nDiagonalData[nDiagonalTest*4];
			// Starting column.
			int nCol = Board.m_nDiagonalData[nDiagonalTest*4+1];
			// YDirection for the iterations.
			int nYDir = Board.m_nDiagonalData[nDiagonalTest*4+2];
			// Number of iterations.
			int nIterations = Board.m_nDiagonalData[nDiagonalTest*4+3];

			// Loop through the iterations.
			for( int i=0; i<nIterations; i++ )
			{
				// If this is nSide then increment the counter.
				if( BoardData[nRow][nCol] == nPiece )
				{
					// Increment the counter.
					nCount++;
				}

				// This square does not equal nSide.
				else
				{
					if( nCount == 2 )
					{
						Twos++;
					}
					else if( nCount == 3 )
					{
						Threes++;
					}
					else if( nCount == 4 )
					{
						Fours++;
					}

					// Reset the counter.
					nCount = 0;
				}

				// Move the row position.
				nRow += nYDir;
				// Move the column position.
				nCol++;
			}

			if( nCount == 2 )
			{
				Twos++;
			}
			else if( nCount == 3 )
			{
				Threes++;
			}
			else if( nCount == 4 )
			{
				Fours++;
			}

		}

		int nPositionalAdvantage = 0;
		for( int nCol=0; nCol<7; nCol++ )
		{
			int nCount = 0;
			for( int nRow=0; nRow<6; nRow++ )
			{
				if( BoardData[nRow][nCol] == nPiece )
				{
					nCount++;
				}
			}

			if( nCol == 2 || nCol == 3 )
			{
				//nPositionalAdvantage += nCount * 2;
			}
			else if( nCol == 1 || nCol == 4 )
			{
				//nPositionalAdvantage += nCount;
			}

		}
		for( int nCol=0; nCol<7; nCol++ )
		{
			int nCount = 0;
			for( int nRow=0; nRow<6; nRow++ )
			{
				if( BoardData[nRow][nCol] != nPiece )
				{
					nCount++;
				}
			}

			if( nCol == 2 || nCol == 3 )
			{
				nPositionalAdvantage -= nCount * 2;
			}
			else if( nCol == 1 || nCol == 4 )
			{
				nPositionalAdvantage -= nCount;
			}

		}

		return( nPositionalAdvantage + Twos + Threes * 2 + Fours * 4 );
	}

	static String ArrayToString( int[] IntArray )
	{
		String strRet = "{ ";
		for( int i=0; i<IntArray.length; i++ )
		{
			strRet += (""+IntArray[i] );
			if( i < IntArray.length - 1 )
			{
				strRet += ", ";
			}
		}
		return( strRet + " }");
	}

}