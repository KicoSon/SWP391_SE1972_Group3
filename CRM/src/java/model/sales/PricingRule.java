package model.sales;

import java.math.BigDecimal;
import java.util.Date;

public class PricingRule {

    private int id;
    private String name;
    private String ruleType; // DISCOUNT / GROUP_PRICE / CAMPAIGN_PRICE
    private BigDecimal value;
    private boolean isPercent;
    private Date startDate;
    private Date endDate;
    private boolean isActive;

    public PricingRule() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRuleType() { return ruleType; }
    public void setRuleType(String ruleType) { this.ruleType = ruleType; }

    public BigDecimal getValue() { return value; }
    public void setValue(BigDecimal value) { this.value = value; }

    public boolean isPercent() { return isPercent; }
    public void setPercent(boolean isPercent) { this.isPercent = isPercent; }

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean isActive) { this.isActive = isActive; }
}
