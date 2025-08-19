package com.pahana.util;

import org.junit.Assert;
import org.junit.Test;

public class PasswordUtilTest {

    @Test
    public void testHashAndVerifySuccessAndMismatch() {
        String password = "S3cret!";
        String hash = PasswordUtil.hash(password);
        Assert.assertNotNull(hash);
        Assert.assertNotEquals("Hash should not equal plain password", password, hash);
        Assert.assertTrue("Correct password should verify against hash", PasswordUtil.verify(password, hash));
        Assert.assertFalse("Wrong password should not verify", PasswordUtil.verify("wrong", hash));
    }

    @Test
    public void testNullHandling() {
        Assert.assertNull("Hashing null should return null", PasswordUtil.hash(null));
        Assert.assertFalse("Verify should be false if candidate is null", PasswordUtil.verify(null, "$2a$10$abcdef"));
        Assert.assertFalse("Verify should be false if stored is null", PasswordUtil.verify("abc", null));
    }

    @Test
    public void testPlainFallbackComparison() {
        String stored = "plainpass";
        Assert.assertTrue(PasswordUtil.verify("plainpass", stored));
        Assert.assertFalse(PasswordUtil.verify("different", stored));
    }
}
