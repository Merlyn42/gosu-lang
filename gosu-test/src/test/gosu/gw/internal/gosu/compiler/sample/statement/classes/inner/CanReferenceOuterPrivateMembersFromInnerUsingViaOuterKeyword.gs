package gw.internal.gosu.compiler.sample.statement.classes.inner

class CanReferenceOuterPrivateMembersFromInnerUsingViaOuterKeyword
{
  var _privateData : String

  construct()
  {
    _privateData = "privateData"
  }

  function makeInner() : Inner
  {
    return new Inner()
  }

  function privateFunction() : String
  {
    return "privateFunction"
  }

  class Inner
  {
    function accessPrivateOuterFunction() : String
    {
      return outer.privateFunction()
    }

    function accessPrivateOuterData() : String
    {
      return outer._privateData
    }
  }
}