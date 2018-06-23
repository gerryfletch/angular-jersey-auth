interface Response {
  status: number;
  statusText: string;
}

export class MockResponse {
  /** Status: 401 */
  public static UNAUTHORIZED: Response = {status: 401, statusText: 'You don\'t have permission for this resource.'};

  /** Status: 400 */
  public static BAD_REQUEST: Response = {status: 400, statusText: 'Bad request.'};

  /** Status: 500 */
  public static INTERNAL_SERVER_ERROR: Response = {status: 500, statusText: 'Internal server error.'};

}
