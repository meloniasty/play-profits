trait DataFixtures {
  val jsonCreateProfitTableRequest =
    """
      |{
      |  "bankId":"someBank",
      |  "columns": [
      |    {
      |      "min":0,
      |      "max":40000
      |    },
      |    {
      |      "min":40000,
      |      "max":70000
      |    }
      |  ],
      |  "rows":[
      |    {
      |      "min":10,
      |      "max":40
      |    },
      |    {
      |      "min":40,
      |      "max":60
      |    }
      |  ],
      |  "values":[
      |    [
      |      1.0,2.3
      |    ],
      |    [
      |      2.7,5.6
      |    ]
      |  ]
      |}
    """.stripMargin

  val jsonCalculateProfitRequest =
    """
      |{
      |"currentMonth":15,
      |"creditAmount":60000,
      |"ownShare":14
      |}
    """.stripMargin

  val jsonCalculateProfitRequest2 =
    """
      |{
      |"currentMonth":3,
      |"creditAmount":60000,
      |"ownShare":14
      |}
    """.stripMargin
}
